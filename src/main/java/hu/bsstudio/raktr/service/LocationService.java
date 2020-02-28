package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LocationService {

    private final LocationDao locationDao;

    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public Location create(Location location) {
        final Location saved = locationDao.save(location);
        log.info("Location created: {}", saved);
        return saved;
    }

    public List<Location> getAll() {
        final List<Location> fetched = locationDao.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }

    public Location update(Location locationRequest) {
        final Location locationToUpdate = locationDao.getOne(locationRequest.getId());
        locationToUpdate.setName(locationRequest.getName());
        final Location updated = locationDao.save(locationToUpdate);
        log.info("Location updated in DB: {}", updated);
        return updated;
    }

    public Location delete(Location locationRequest) {
        final Location locationToDelete = locationDao.getOne(locationRequest.getId());
        locationDao.delete(locationToDelete);
        log.info("Location deleted: {}", locationToDelete);
        return locationToDelete;
    }
}
