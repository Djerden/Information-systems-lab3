package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.CoordinatesRepository;
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
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final UserService userService;

    // Создание Coordinates
    public Coordinates createCoordinates(Coordinates coordinates) {
        var currentUser = userService.getCurrentUser();
        coordinates.setUser(currentUser); // Устанавливаем владельца
        return coordinatesRepository.save(coordinates);
    }

    // Получение Coordinates по ID с проверкой доступа
    public Coordinates getCoordinatesById(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordinates not found with id: " + id));
        return coordinates;
    }

    public Page<Coordinates> getAllCoordinates(Pageable pageable) {
        return coordinatesRepository.findAll(pageable);
    }

    // Обновление Coordinates
    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates) {
        Coordinates existingCoordinates = getCoordinatesById(id);
        checkAccess(existingCoordinates);

        existingCoordinates.setX(updatedCoordinates.getX());
        existingCoordinates.setY(updatedCoordinates.getY());

        return coordinatesRepository.save(existingCoordinates);
    }

    // Удаление Coordinates
    public void deleteCoordinates(Long id) {
        Coordinates coordinates = getCoordinatesById(id);
        checkAccess(coordinates);
        coordinatesRepository.delete(coordinates);
    }

    // Проверка доступа
    private void checkAccess(Coordinates coordinates) {
        var currentUser = userService.getCurrentUser();
        if (!coordinates.getUser().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new RuntimeException("Access denied");
        }
    }
}
