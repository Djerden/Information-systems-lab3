package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.DTO.ImportHistoryDTO;
import com.djeno.backend_lab1.DTO.PersonDTO;
import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.exceptions.TooManyRequestsException;
import com.djeno.backend_lab1.models.*;
import com.djeno.backend_lab1.models.enums.ImportStatus;
import com.djeno.backend_lab1.service.ImportService;
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
import org.springframework.data.domain.Page;
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
@RequestMapping("import")
@RequiredArgsConstructor
public class ImportController {

    private final UserService userService;
    private final ImportHistoryService importHistoryService;
    private final ImportService importService;
    private final UserRequestLimiter userRequestLimiter;

    private final Semaphore semaphore = new Semaphore(20); // Максимум запросов с файлами до 6мб

    @PostMapping(value = "/yaml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ResponseEntity<?> importYaml(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {

        if (!semaphore.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many concurrent requests. Please try again later.");
        }

        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        try {
            userRequestLimiter.acquirePermission(userId);

            ImportHistory importHistory = ImportHistory.builder()
                    .user(currentUser)
                    .status(ImportStatus.PROCESSING)
                    .timestamp(LocalDateTime.now())
                    .addedObjects(0)
                    .build();
            importHistory = importHistoryService.saveImportHistory(importHistory);

            // Асинхронное выполнение импорта данных
            CompletableFuture<Integer> addedObjects = importService.importYamlData(file.getInputStream(), currentUser);

            // Обрабатываем завершение асинхронной операции
            ImportHistory finalImportHistory = importHistory;
            addedObjects.whenComplete((result, exception) -> {
                try {
                    if (exception == null) {
                        finalImportHistory.setStatus(ImportStatus.SUCCESS);
                        finalImportHistory.setAddedObjects(result);
                    } else {
                        finalImportHistory.setStatus(ImportStatus.FAILED);
                    }
                    importHistoryService.saveImportHistory(finalImportHistory); // Сохраняем результат
                } finally {
                    // Освобождаем разрешение пользователя после завершения
                    userRequestLimiter.releasePermission(userId);
                    // Освобождаем разрешение для запроса
                    semaphore.release();
                }
            });

            return ResponseEntity.ok("Data is being processed. You will be see in history");

        } catch (TooManyRequestsException e) {
            // Если пользователь превысил лимит запросов
            semaphore.release(); // Освобождаем семафор в случае исключения
            throw e;
        } catch (Exception e) {
            semaphore.release();
            userRequestLimiter.releasePermission(userId); // Освобождаем разрешение пользователя
            throw e; // Пробрасываем исключение дальше
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
}
