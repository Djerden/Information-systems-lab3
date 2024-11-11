package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Location updateLocation(Long id, Location updatedLocation) {
        if (locationRepository.existsById(id)) {
            updatedLocation.setId(id);
            return locationRepository.save(updatedLocation);
        }
        return null;
    }

//    public List<Location> findByCoordinates(float x, int y) {
//        return locationRepository.findByXAndY(x, y);
//    }
}
