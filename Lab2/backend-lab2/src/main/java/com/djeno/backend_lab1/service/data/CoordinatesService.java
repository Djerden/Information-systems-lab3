package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.CoordinatesRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final UserService userService;


    public boolean existsByXAndY(float x, double y) {
        return coordinatesRepository.existsByXAndY(x, y);
    }

    public List<Coordinates> saveAll(List<Coordinates> coordinatesList) {
        return coordinatesRepository.saveAll(coordinatesList);
    }

    // Создание Coordinates
    @Transactional(isolation = Isolation.SERIALIZABLE)
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

    public List<Coordinates> getAllCoordinates(int count) {
        if (count > 0) {
            Pageable pageable = PageRequest.of(0, count);
            return coordinatesRepository.findAll(pageable).getContent();
        } else {
            // Если count <= 0, возвращаем все записи
            return coordinatesRepository.findAll();
        }
    }

    // Обновление Coordinates
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates) {
        Coordinates existingCoordinates = getCoordinatesById(id);
        checkAccess(existingCoordinates);

        existingCoordinates.setX(updatedCoordinates.getX());
        existingCoordinates.setY(updatedCoordinates.getY());

        return coordinatesRepository.save(existingCoordinates);
    }

    // Удаление Coordinates
    @Transactional(isolation = Isolation.SERIALIZABLE)
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
