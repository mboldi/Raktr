package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.LocationRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Location;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(final LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public final Location create(final Location location) {
        final Location saved = locationRepository.save(location);
        log.info("Location created: {}", saved);
        return saved;
    }

    public final List<Location> getAll() {
        final List<Location> fetched = locationRepository.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }

    public final Location update(final Location locationRequest) {
        final Location locationToUpdate = locationRepository.findById(locationRequest.getId()).orElse(null);

        if (locationToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        locationToUpdate.setName(locationRequest.getName());

        final Location updated = locationRepository.save(locationToUpdate);
        log.info("Location updated in DB: {}", updated);
        return updated;
    }

    public final Location delete(final Location locationRequest) {
        locationRepository.delete(locationRequest);
        log.info("Location deleted: {}", locationRequest);
        return locationRequest;
    }
}
