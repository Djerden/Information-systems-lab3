package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.exceptions.LocationNameAlreadyExistsException;
import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.LocationRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserService userService;

    public List<Location> saveAll(List<Location> locationList) {
        return locationRepository.saveAll(locationList);
    }

    public boolean existsByName(String name) {
        return locationRepository.existsByName(name);
    }

    // Создание Location
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location createLocation(Location location) {
        validateUniqueLocationName(location.getName()); // проверка уникальности имени
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location updateLocation(Long id, Location updatedLocation) {
        Location existingLocation = getLocationById(id);
        checkAccess(existingLocation);

        // Проверяем, изменилось ли имя, и если да, то выполняем проверку на уникальность
        if (updatedLocation.getName() != null && !updatedLocation.getName().equals(existingLocation.getName())) {
            validateUniqueLocationName(updatedLocation.getName()); // Проверка уникальности имени
        }

        existingLocation.setX(updatedLocation.getX());
        existingLocation.setY(updatedLocation.getY());
        // Обновление имени, если оно изменилось
        if (updatedLocation.getName() != null && !updatedLocation.getName().equals(existingLocation.getName())) {
            existingLocation.setName(updatedLocation.getName());
        }

        return locationRepository.save(existingLocation);
    }

    // Удаление Location
    @Transactional(isolation = Isolation.SERIALIZABLE)
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

    public void validateUniqueLocationName(String name) {
        if (locationRepository.existsByName(name)) {
            throw new LocationNameAlreadyExistsException(name);
        }
    }
}
