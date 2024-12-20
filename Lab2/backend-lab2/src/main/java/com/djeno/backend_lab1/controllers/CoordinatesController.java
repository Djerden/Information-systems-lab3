package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.service.data.CoordinatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/coordinates")
@RequiredArgsConstructor
public class CoordinatesController {

    private final CoordinatesService coordinatesService;

    @PostMapping
    public ResponseEntity<Coordinates> createCoordinates(@RequestBody Coordinates coordinates) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordinatesService.createCoordinates(coordinates));
    }

    @GetMapping
    public ResponseEntity<List<Coordinates>> getAllCoordinates() {
        return ResponseEntity.ok(coordinatesService.getAllCoordinates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coordinates> getCoordinatesById(@PathVariable Long id) {
        return ResponseEntity.ok(coordinatesService.getCoordinatesById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coordinates> updateCoordinates(@PathVariable Long id, @RequestBody Coordinates coordinates) {
        return ResponseEntity.ok(coordinatesService.updateCoordinates(id, coordinates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoordinates(@PathVariable Long id) {
        coordinatesService.deleteCoordinates(id);
        return ResponseEntity.noContent().build();
    }
}
