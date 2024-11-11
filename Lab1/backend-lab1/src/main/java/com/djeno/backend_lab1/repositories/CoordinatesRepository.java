package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {

    //Например, найти все координаты с определенным значением x
    List<Coordinates> findByX(float x);

    // Найти все координаты с y больше заданного значения
    List<Coordinates> findByYGreaterThan(double y);
}

