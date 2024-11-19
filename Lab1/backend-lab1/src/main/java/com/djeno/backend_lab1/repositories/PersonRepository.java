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

    // Проверить принадлежность объекта пользователю
    boolean existsByIdAndUserId(Long id, Long userId);

}

