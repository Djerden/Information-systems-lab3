package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.models.enums.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Найти всех людей с заданным именем
    List<Person> findByName(String name);

    // Найти всех людей с определенным цветом глаз
    List<Person> findByEyeColor(Color eyeColor);

    // Найти всех людей с определенным цветом волос
    List<Person> findByHairColor(Color hairColor);

    // Найти всех людей по локации
    List<Person> findByLocation(Location location);

    // Найти всех людей с весом больше заданного значения
    List<Person> findByWeightGreaterThan(float weight);

    // Найти всех людей по национальности
    List<Person> findByNationality(Country nationality);
}

