package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Найти все записи с определенным значением x
    List<Location> findByX(Float x);

    // Найти все записи с определенным значением y
    List<Location> findByY(Integer y);

    // Найти все записи, у которых поле name совпадает с заданным значением
    List<Location> findByName(String name);

    // Найти все записи, у которых поле name содержит заданную подстроку
    List<Location> findByNameContaining(String substring);
}
