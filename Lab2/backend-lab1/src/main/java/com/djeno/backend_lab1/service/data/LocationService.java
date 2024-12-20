package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.LocationRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserService userService;

    // Создание Location
    public Location createLocation(Location location) {
        var currentUser = userService.getCurrentUser();
        location.setUser(currentUser); // Устанавливаем владельца
        return locationRepository.save(location);
    }

    // Получение Location по ID с проверкой доступа
    public Location getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return location;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    // Обновление Location
    public Location updateLocation(Long id, Location updatedLocation) {
        Location existingLocation = getLocationById(id);
        checkAccess(existingLocation);

        existingLocation.setX(updatedLocation.getX());
        existingLocation.setY(updatedLocation.getY());
        existingLocation.setName(updatedLocation.getName());

        return locationRepository.save(existingLocation);
    }

    // Удаление Location
    public void deleteLocation(Long id) {
        Location location = getLocationById(id);
        checkAccess(location);
        locationRepository.delete(location);
    }

    // Проверка доступа
    private void checkAccess(Location location) {
        var currentUser = userService.getCurrentUser();
        if (!location.getUser().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new RuntimeException("Access denied");
        }
    }
}
