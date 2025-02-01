package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.DTO.ImportHistoryDTO;
import com.djeno.backend_lab1.DTO.PersonDTO;
import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.models.*;
import com.djeno.backend_lab1.models.enums.ImportStatus;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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

    @PostMapping(value = "/yaml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> importYaml(@RequestParam("file") MultipartFile file) {

        User currentUser = userService.getCurrentUser();

        ImportHistory importHistory = ImportHistory.builder()
                .user(currentUser)
                .status(ImportStatus.PROCESSING)
                .timestamp(LocalDateTime.now())
                .addedObjects(0)
                .build();

        try {
            importHistory = importHistoryService.saveImportHistory(importHistory); // Сохраняем историю с начальным статусом

            int addedObjects = 0; // Переменная для подсчета добавленных объектов

            // Используем потоковую обработку для чтения файла
            try (InputStream inputStream = file.getInputStream()) {
                YAMLFactory yamlFactory = new YAMLFactory();
                yamlFactory.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
                // Настройка для работы с большими числами
                yamlMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);  // Используем BigInteger для целых чисел
                yamlMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);  // Используем BigDecimal для чисел с плавающей запятой
                JsonParser jsonParser = yamlFactory.createParser(inputStream);

                // Перейти к первому токену и проверить, что это массив
                if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                    throw new RuntimeException("YAML root element must be an array.");
                }

                // Чтение массива объектов
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    // Парсим один объект
                    Map<String, Object> group = yamlMapper.readValue(jsonParser, new TypeReference<>() {});

                    System.out.println("Parsed group data: {} " +  group);

                    // Создание Coordinates
                    Map<String, Object> coordinatesData = (Map<String, Object>) group.get("coordinates");
                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(((Number) coordinatesData.get("x")).floatValue());
                    coordinates.setY(((Number) coordinatesData.get("y")).doubleValue());
                    Coordinates savedCoordinates = coordinatesService.createCoordinates(coordinates);

                    System.out.println("Saved coordinates: {} " +  savedCoordinates);

                    // Создание GroupAdmin
                    Map<String, Object> adminData = (Map<String, Object>) group.get("group_admin");
                    Map<String, Object> locationData = (Map<String, Object>) adminData.get("location");

                    Location location = new Location();
                    location.setX(((Number) locationData.get("x")).floatValue());
                    location.setY(((Number) locationData.get("y")).intValue());
                    location.setName((String) locationData.get("name"));
                    Location savedLocation = locationService.createLocation(location);

                    System.out.println("Saved locations: {} " +  savedLocation);

                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setName((String) adminData.get("name"));
                    personDTO.setEyeColor((String) adminData.get("eye_color"));
                    personDTO.setHairColor((String) adminData.get("hair_color"));
                    personDTO.setWeight(((Number) adminData.get("weight")).floatValue());
                    personDTO.setNationality((String) adminData.get("nationality"));
                    personDTO.setLocationId(savedLocation.getId());
                    Person savedAdmin = personService.createPerson(personService.fromDTO(personDTO));

                    System.out.println("Saved admins: {} " +  savedAdmin);

                    // Создание StudyGroup
                    StudyGroupDTO groupDTO = new StudyGroupDTO();
                    groupDTO.setName((String) group.get("name"));
                    groupDTO.setCoordinatesId(savedCoordinates.getId());
                    groupDTO.setGroupAdminId(savedAdmin.getId());
                    groupDTO.setStudentsCount(((Number) group.get("students_count")).intValue());
                    groupDTO.setExpelledStudents(((Number) group.get("expelled_students")).intValue());
                    groupDTO.setShouldBeExpelled(((Number) group.get("should_be_expelled")).longValue());
                    groupDTO.setTransferredStudents(((Number) group.get("transferred_students")).longValue());
                    groupDTO.setFormOfEducation((String) group.get("form_of_education"));
                    groupDTO.setSemesterEnum((String) group.get("semester_enum"));
                    studyGroupService.createStudyGroup(groupDTO);

                    System.out.println("Saved study groups: {} " +  groupDTO);

                    addedObjects++;
                }
            }

            importHistory.setStatus(ImportStatus.SUCCESS);
            importHistory.setAddedObjects(addedObjects);
            importHistoryService.saveImportHistory(importHistory);

            return ResponseEntity.ok("Data imported successfully");
        } catch (Exception e) {
            importHistory.setStatus(ImportStatus.FAILED); // Если ошибка, статус будет FAILED
            importHistoryService.saveImportHistory(importHistory); // Сохраняем историю с ошибкой
            throw new RuntimeException("Error during import: " + e.getMessage(), e);
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
