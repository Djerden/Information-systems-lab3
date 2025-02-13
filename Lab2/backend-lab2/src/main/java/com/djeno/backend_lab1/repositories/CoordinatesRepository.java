package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);

    boolean existsByXAndY(float x, double y);
}

