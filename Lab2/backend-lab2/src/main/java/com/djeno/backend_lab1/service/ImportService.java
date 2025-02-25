package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.DTO.DataContainer;
import com.djeno.backend_lab1.DTO.PersonDTO;
import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.exceptions.DublicateFileException;
import com.djeno.backend_lab1.models.*;
import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.models.enums.Country;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import com.djeno.backend_lab1.service.data.CoordinatesService;
import com.djeno.backend_lab1.service.data.LocationService;
import com.djeno.backend_lab1.service.data.PersonService;
import com.djeno.backend_lab1.service.data.StudyGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class ImportService {

    private final CoordinatesService coordinatesService;
    private final LocationService locationService;
    private final PersonService personService;
    private final StudyGroupService studyGroupService;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final UserService userService;

    // prepare метод
    public DataContainer parseYamlFileAndCheckConstraints(MultipartFile file) throws IOException {
        User user = userService.getCurrentUser();

        InputStream inputStream = file.getInputStream();

        List<Coordinates> coordinatesList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        List<Person> personList = new ArrayList<>();
        List<StudyGroup> studyGroupList = new ArrayList<>();

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setCodePointLimit(Integer.MAX_VALUE); // Увеличиваем лимит

        Yaml yaml = new Yaml(new SafeConstructor(loaderOptions));

        Iterable<Object> yamlDocuments = yaml.loadAll(inputStream);

        // Обработка документов
        for (Object document : yamlDocuments) {
            if (document instanceof List) {
                List<Map<String, Object>> groups = (List<Map<String, Object>>) document;
                for (Map<String, Object> group : groups) {
                    // Проверка уникальности StudyGroup
                    String studyGroupName = (String) group.get("name");
                    if (studyGroupService.existsByName(studyGroupName)) {
                        throw new DublicateFileException("StudyGroup with name '" + studyGroupName + "' already exists.");
                    }

                    // Создание Coordinates
                    Map<String, Object> coordinatesData = (Map<String, Object>) group.get("coordinates");
                    Coordinates coordinates = null;
                    if (coordinatesData != null) {
                        float x = ((Number) coordinatesData.get("x")).floatValue();
                        double y = ((Number) coordinatesData.get("y")).doubleValue();
                        if (coordinatesService.existsByXAndY(x, y)) {
                            throw new DublicateFileException("Coordinates with x=" + x + " and y=" + y + " already exists.");
                        }
                        coordinates = new Coordinates();
                        coordinates.setX(x);
                        coordinates.setY(y);
                        coordinates.setUser(user);
                        coordinatesList.add(coordinates);
                    }

                    // Создание Location
                    Map<String, Object> adminData = (Map<String, Object>) group.get("group_admin");
                    Location location = null;
                    if (adminData != null) {
                        Map<String, Object> locationData = (Map<String, Object>) adminData.get("location");
                        if (locationData != null) {
                            String locationName = (String) locationData.get("name");
                            if (locationService.existsByName(locationName)) {
                                throw new DublicateFileException("Location with name '" + locationName + "' already exists.");
                            }
                            location = new Location();
                            location.setX(((Number) locationData.get("x")).floatValue());
                            location.setY(((Number) locationData.get("y")).intValue());
                            location.setName(locationName);
                            location.setUser(user);
                            locationList.add(location);
                        }
                    }

                    if (adminData != null) {
                        Person person = new Person();
                        person.setName((String) adminData.get("name"));
                        person.setEyeColor(Color.valueOf((String) adminData.get("eye_color")));
                        person.setHairColor(Color.valueOf((String) adminData.get("hair_color")));
                        person.setWeight(((Number) adminData.get("weight")).floatValue());
                        person.setNationality(Country.valueOf((String) adminData.get("nationality")));
                        person.setLocation(location); // Устанавливаем связь с Location
                        person.setUser(user); // Устанавливаем владельца
                        personList.add(person);
                    }

                    StudyGroup studyGroup = new StudyGroup();
                    studyGroup.setName(studyGroupName);
                    studyGroup.setCreationDate(LocalDate.now()); // Устанавливаем текущую дату
                    studyGroup.setStudentsCount(((Number) group.get("students_count")).longValue());
                    studyGroup.setExpelledStudents(((Number) group.get("expelled_students")).intValue());
                    studyGroup.setTransferredStudents(((Number) group.get("transferred_students")).longValue());
                    studyGroup.setFormOfEducation(FormOfEducation.valueOf((String) group.get("form_of_education")));
                    studyGroup.setShouldBeExpelled(((Number) group.get("should_be_expelled")).longValue());
                    studyGroup.setSemesterEnum(Semester.valueOf((String) group.get("semester_enum")));
                    studyGroup.setUser(user); // Устанавливаем владельца
                    studyGroupList.add(studyGroup);
                }
            }
        }
        DataContainer dataContainer = new DataContainer(coordinatesList, locationList, personList, studyGroupList);
        return dataContainer;
    }

    // commit метод
    public int saveData(DataContainer dataContainer) {
        List<Coordinates> savedCoordinates = coordinatesService.saveAll(dataContainer.getCoordinatesList());
        List<Location> savedLocations = locationService.saveAll(dataContainer.getLocationList());
        List<Person> savedPersons = personService.saveAll(dataContainer.getPersonList());
        // Связывание StudyGroup с сохраненными объектами
        for (int i = 0; i < dataContainer.getStudyGroupList().size(); i++) {
            StudyGroup studyGroup = dataContainer.getStudyGroupList().get(i);
            studyGroup.setCoordinates(savedCoordinates.get(i));
            studyGroup.setGroupAdmin(savedPersons.get(i));
        }
        studyGroupService.saveAll(dataContainer.getStudyGroupList());

        return dataContainer.getStudyGroupList().size();
    }

    private Coordinates createCoordinates(Map<String, Object> group) {
        Map<String, Object> coordinatesData = (Map<String, Object>) group.get("coordinates");
        Coordinates coordinates = new Coordinates();
        coordinates.setX(((Number) coordinatesData.get("x")).floatValue());
        coordinates.setY(((Number) coordinatesData.get("y")).doubleValue());
        return coordinatesService.createCoordinates(coordinates);
    }

    private Person createGroupAdmin(Map<String, Object> group) {
        Map<String, Object> adminData = (Map<String, Object>) group.get("group_admin");
        Map<String, Object> locationData = (Map<String, Object>) adminData.get("location");

        Location location = new Location();
        location.setX(((Number) locationData.get("x")).floatValue());
        location.setY(((Number) locationData.get("y")).intValue());
        location.setName((String) locationData.get("name"));
        Location savedLocation = locationService.createLocation(location);

        PersonDTO personDTO = new PersonDTO();
        personDTO.setName((String) adminData.get("name"));
        personDTO.setEyeColor((String) adminData.get("eye_color"));
        personDTO.setHairColor((String) adminData.get("hair_color"));
        personDTO.setWeight(((Number) adminData.get("weight")).floatValue());
        personDTO.setNationality((String) adminData.get("nationality"));
        personDTO.setLocationId(savedLocation.getId());

        return personService.createPerson(personService.fromDTO(personDTO));
    }

    private void createStudyGroup(Map<String, Object> group, Coordinates coordinates, Person admin) {
        StudyGroupDTO groupDTO = new StudyGroupDTO();
        groupDTO.setName((String) group.get("name"));
        groupDTO.setCoordinatesId(coordinates.getId());
        groupDTO.setGroupAdminId(admin.getId());
        groupDTO.setStudentsCount(((Number) group.get("students_count")).intValue());
        groupDTO.setExpelledStudents(((Number) group.get("expelled_students")).intValue());
        groupDTO.setShouldBeExpelled(((Number) group.get("should_be_expelled")).longValue());
        groupDTO.setTransferredStudents(((Number) group.get("transferred_students")).longValue());
        groupDTO.setFormOfEducation((String) group.get("form_of_education"));
        groupDTO.setSemesterEnum((String) group.get("semester_enum"));
        studyGroupService.createStudyGroup(groupDTO);
    }
}
