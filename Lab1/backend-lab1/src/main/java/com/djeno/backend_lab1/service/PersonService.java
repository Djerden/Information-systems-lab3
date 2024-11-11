package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public List<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable).getContent();
    }

    public Person updatePerson(Long id, Person updatedPerson) {
        if (personRepository.existsById(id)) {
            updatedPerson.setId(id);
            return personRepository.save(updatedPerson);
        }
        return null;
    }

    public List<Person> findByEyeColor(Color eyeColor) {
        return personRepository.findByEyeColor(eyeColor);
    }

    public List<Person> findByHairColor(Color hairColor) {
        return personRepository.findByHairColor(hairColor);
    }
}
