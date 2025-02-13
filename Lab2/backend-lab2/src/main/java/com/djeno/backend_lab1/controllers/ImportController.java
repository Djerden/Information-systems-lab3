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

@RestController
@RequestMapping("import")
@RequiredArgsConstructor
public class ImportController {

    private final CoordinatesService coordinatesService;
    private final LocationService locationService;
    private final PersonService personService;
    private final StudyGroupService studyGroupService;
    private final UserService userService;
    private final ImportHistoryService importHistoryService;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ImportService importService;
    private final UserRequestLimiter userRequestLimiter;

    @PostMapping(value = "/yaml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<?> importYaml(@RequestParam("file") MultipartFile file) throws IOException {

        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        try {
            // Пытаемся получить разрешение на выполнение запроса
            userRequestLimiter.acquirePermission(userId);

            ImportHistory importHistory = ImportHistory.builder()
                    .user(currentUser)
                    .status(ImportStatus.PROCESSING)
                    .timestamp(LocalDateTime.now())
                    .addedObjects(0)
                    .build();

            try {
                importHistory = importHistoryService.saveImportHistory(importHistory); // Сохраняем историю с начальным статусом

                int addedObjects = importService.importYamlData(file.getInputStream(), currentUser);

                importHistory.setStatus(ImportStatus.SUCCESS);
                importHistory.setAddedObjects(addedObjects);
                importHistoryService.saveImportHistory(importHistory);

                return ResponseEntity.ok("Data imported successfully");
            } catch (Exception e) {
                importHistory.setStatus(ImportStatus.FAILED); // Если ошибка, статус будет FAILED
                importHistoryService.saveImportHistory(importHistory); // Сохраняем историю с ошибкой
                throw e;
            } finally {
                // Освобождаем разрешение после завершения запроса
                userRequestLimiter.releasePermission(userId);
            }
        } catch (TooManyRequestsException e) {
            // Если пользователь превысил лимит запросов
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
}
