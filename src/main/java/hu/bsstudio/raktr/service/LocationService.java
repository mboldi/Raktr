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
}
