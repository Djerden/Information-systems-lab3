package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.DTO.ImportHistoryDTO;
import com.djeno.backend_lab1.DTO.PersonDTO;
import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.exceptions.DublicateFileException;
import com.djeno.backend_lab1.exceptions.TooManyRequestsException;
import com.djeno.backend_lab1.models.*;
import com.djeno.backend_lab1.models.enums.ImportStatus;
import com.djeno.backend_lab1.service.ImportService;
import com.djeno.backend_lab1.service.MinioService;
import com.djeno.backend_lab1.service.UserRequestLimiter;
import com.djeno.backend_lab1.service.UserService;
import com.djeno.backend_lab1.service.data.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final UserService userService;
    private final ImportHistoryService importHistoryService;
    private final ImportService importService;
    private final UserRequestLimiter userRequestLimiter;
    private final MinioService minioService;

    private final Semaphore semaphore = new Semaphore(20); // Максимум запросов с файлами до 6мб

    @PostMapping(value = "/yaml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ResponseEntity<?> importYaml(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {

        if (!semaphore.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many concurrent requests. Please try again later.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".yaml") && !filename.endsWith(".yml"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only .yaml or .yml files are allowed.");
        }

        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        ImportHistory importHistory = null;

        try {
            userRequestLimiter.acquirePermission(userId);

            importHistory = ImportHistory.builder()
                    .user(currentUser)
                    .status(ImportStatus.PROCESSING)
                    .timestamp(LocalDateTime.now())
                    .addedObjects(0)
                    .fileName(filename)
                    .fileUrl(null)
                    .build();
            importHistory = importHistoryService.saveImportHistory(importHistory);

            String fileUrl = minioService.uploadFile(file); // Загрузка файла в Minio, уникальное имя никуда не сохранено

            int addedObjects = importService.importYamlData(file.getInputStream(), currentUser);

            importHistory.setStatus(ImportStatus.SUCCESS);
            importHistory.setAddedObjects(addedObjects);
            importHistory.setFileUrl(fileUrl);
            importHistoryService.saveImportHistory(importHistory);

            semaphore.release();
            userRequestLimiter.releasePermission(userId);

            return ResponseEntity.ok("Data saved successfully.");

        } catch (TooManyRequestsException e) {
            semaphore.release();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (DublicateFileException e) {
            if(importHistory != null) {
                importHistory.setStatus(ImportStatus.FAILED);
                importHistoryService.saveImportHistory(importHistory);
            }
            semaphore.release();
            userRequestLimiter.releasePermission(userId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            if (importHistory != null) {
                importHistory.setStatus(ImportStatus.FAILED);
                importHistoryService.saveImportHistory(importHistory);  // Записываем статус ошибки
            }
            semaphore.release();
            userRequestLimiter.releasePermission(userId);
            throw e;
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
            InputStream inputStream = minioService.downloadFile(fileUrl);

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
