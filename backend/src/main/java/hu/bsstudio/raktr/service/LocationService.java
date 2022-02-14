package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public final Optional<Location> create(final Location location) {
        final var found = locationRepository.findByName(location.getName());

        if (found.isEmpty()) {
            final Location saved = locationRepository.save(location);
            log.info("Location created: {}", saved);
            return Optional.of(saved);
        } else {
            log.info("Location exists with given new name: {}", location.getName());
        }

        return found;
    }

    public final List<Location> getAll() {
        final List<Location> fetched = locationRepository.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }

    public final Optional<Location> update(final Location locationRequest) {
        final var locationToUpdate = locationRepository.findById(locationRequest.getId());

        if (locationToUpdate.isEmpty()) {
            return locationToUpdate;
        }

        locationToUpdate.get().setName(locationRequest.getName());

        final Location updated = locationRepository.save(locationToUpdate.get());
        log.info("Location updated in DB: {}", updated);
        return Optional.of(updated);
    }

    public final Optional<Location> delete(final Location locationRequest) {
        final var found = locationRepository.findById(locationRequest.getId());

        if (found.isPresent()) {
            locationRepository.delete(locationRequest);
            log.info("Location deleted: {}", locationRequest);
        } else {
            log.info("Location not found to delete by id: {}", locationRequest);
        }

        return found;
    }
}
