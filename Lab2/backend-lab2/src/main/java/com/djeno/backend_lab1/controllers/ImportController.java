package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.DTO.ImportHistoryDTO;
import com.djeno.backend_lab1.exceptions.TooManyRequestsException;
import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.service.MinioService;
import com.djeno.backend_lab1.service.UserRequestLimiter;
import com.djeno.backend_lab1.service.UserService;
import com.djeno.backend_lab1.service.data.*;
import com.djeno.backend_lab1.service.saga.listeners.ImportHistoryEventListener;
import com.djeno.backend_lab1.service.saga.listeners.MinioEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final UserService userService;
    private final ImportHistoryService importHistoryService;
    private final UserRequestLimiter userRequestLimiter;
    private final MinioService minioService;
    private final ImportHistoryEventListener importHistoryEventListener;
    private final MinioEventListener minioEventListener;

    private final Semaphore semaphore = new Semaphore(20); // Максимум запросов с файлами до 6мб

    @PostMapping(value = "/yaml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importYaml(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {

        if (!semaphore.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many concurrent requests. Please try again later.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".yaml") && !fileName.endsWith(".yml"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only .yaml or .yml files are allowed.");
        }

        Long userId = userService.getCurrentUser().getId();

        try {
            userRequestLimiter.acquirePermission(userId);

            ImportHistory importHistory = importHistoryEventListener.createRecordImportHistory(fileName);
            minioEventListener.handleHistoryCreatedEvent(file, importHistory);

            semaphore.release();
            userRequestLimiter.releasePermission(userId);

            return ResponseEntity.ok("Data saved successfully.");

        } catch (TooManyRequestsException e) {
            semaphore.release();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (Exception e) {
            semaphore.release();
            userRequestLimiter.releasePermission(userId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Эндпоинт для получения истории импорта пользователя
    @GetMapping("/history/user")
    public ResponseEntity<?> getUserImportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String status) {

        Page<ImportHistoryDTO> history = importHistoryService.getHistoryByUser(page, size, sortBy, direction, status);
        return ResponseEntity.ok(history);
    }

    // Эндпоинт для получения истории всех пользователей для админов
    @GetMapping("/history/admin")
    public ResponseEntity<?> getAdminImportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String status) {

        Page<ImportHistoryDTO> history = importHistoryService.getAllHistory(page, size, sortBy, direction, status);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/download/{fileUrl}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileUrl) {
        try {
            InputStream inputStream = minioService.downloadFile(fileUrl, MinioService.IMPORTED_FILES);

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileUrl + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
