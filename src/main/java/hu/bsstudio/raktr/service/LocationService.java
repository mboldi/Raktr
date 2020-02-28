package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Location;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class LocationService {

    private final LocationDao locationDao;

    public LocationService(final LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public Location create(final Location location) {
        final Location saved = locationDao.save(location);
        log.info("Location created: {}", saved);
        return saved;
    }

    public List<Location> getAll() {
        final List<Location> fetched = locationDao.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }

    public Location update(final Location locationRequest) {
        final Location locationToUpdate = locationDao.getOne(locationRequest.getId());
        locationToUpdate.setName(locationRequest.getName());
        final Location updated = locationDao.save(locationToUpdate);
        log.info("Location updated in DB: {}", updated);
        return updated;
    }

    public Location delete(final Location locationRequest) {
        locationDao.delete(locationRequest);
        log.info("Location deleted: {}", locationRequest);
        return locationRequest;
    }
}
