package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.DTO.PersonDTO;
import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.models.enums.Country;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.LocationRepository;
import com.djeno.backend_lab1.repositories.PersonRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;

    public List<Person> saveAll(List<Person> personList) {
        return personRepository.saveAll(personList);
    }

    // Создание Person
    public Person createPerson(Person person) {
        var currentUser = userService.getCurrentUser();
        person.setUser(currentUser);

        // Получаем объект Location по id
        if (person.getLocation() != null && person.getLocation().getId() != null) {
            Location location = locationRepository.findById(person.getLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location ID: " + person.getLocation().getId()));
            person.setLocation(location);
        } else {
            person.setLocation(null); // Если locationId не передан
        }

        return personRepository.save(person);
    }

    public Person fromDTO(PersonDTO dto) {
        Person person = new Person();
        person.setName(dto.getName());
        person.setEyeColor(Color.valueOf(dto.getEyeColor()));
        person.setHairColor(Color.valueOf(dto.getHairColor()));
        person.setWeight(dto.getWeight());
        person.setNationality(Country.valueOf(dto.getNationality()));

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location ID: " + dto.getLocationId()));
            person.setLocation(location);
        }

        return person;
    }



    public Person getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        return person;
    }

    public List<Person> getAllPersons(int count) {
        if (count > 0) {
            // Ограничиваем количество возвращаемых объектов
            Pageable pageable = PageRequest.of(0, count);
            return personRepository.findAll(pageable).getContent();
        } else {
            // Если count <= 0, возвращаем все записи
            return personRepository.findAll();
        }
    }

    // Обновление Person
    public Person updatePerson(Long id, Person updatedPerson) {
        Person existingPerson = getPersonById(id);
        checkAccess(existingPerson);

        existingPerson.setName(updatedPerson.getName());
        existingPerson.setEyeColor(updatedPerson.getEyeColor());
        existingPerson.setHairColor(updatedPerson.getHairColor());
        existingPerson.setLocation(updatedPerson.getLocation());
        existingPerson.setWeight(updatedPerson.getWeight());
        existingPerson.setNationality(updatedPerson.getNationality());

        return personRepository.save(existingPerson);
    }

    // Удаление Person
    public void deletePerson(Long id) {
        Person person = getPersonById(id);
        checkAccess(person);
        personRepository.delete(person);
    }

    // Проверка доступа
    private void checkAccess(Person person) {
        var currentUser = userService.getCurrentUser();
        if (!person.getUser().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new RuntimeException("Access denied");
        }
    }

}
