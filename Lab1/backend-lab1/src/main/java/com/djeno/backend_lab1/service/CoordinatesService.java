package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.repositories.CoordinatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;

    @Autowired
    public CoordinatesService(CoordinatesRepository coordinatesRepository) {
        this.coordinatesRepository = coordinatesRepository;
    }

    public Coordinates createCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    public Optional<Coordinates> getCoordinatesById(Long id) {
        return coordinatesRepository.findById(id);
    }

    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates) {
        if (coordinatesRepository.existsById(id)) {
            updatedCoordinates.setId(id);
            return coordinatesRepository.save(updatedCoordinates);
        }
        return null;
    }

    public List<Coordinates> findAllCoordinates() {
        return coordinatesRepository.findAll();
    }
}
