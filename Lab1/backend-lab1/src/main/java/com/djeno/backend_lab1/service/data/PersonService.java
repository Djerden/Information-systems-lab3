package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.PersonRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserService userService;

    // Создание Person
    public Person createPerson(Person person) {
        var currentUser = userService.getCurrentUser();
        person.setUser(currentUser); // Устанавливаем владельца
        return personRepository.save(person);
    }

    // Получение Person по ID с проверкой доступа
    public Person getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        return person;
    }

    public Page<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
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
