package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.CompositeItemDao;
import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompositeService {

    private final CompositeItemDao compositeItemDao;
    private final DeviceDao deviceDao;
    private final LocationDao locationDao;

    public CompositeService(final CompositeItemDao compositeItemDao, final DeviceDao deviceDao, final LocationDao locationDao) {
        this.compositeItemDao = compositeItemDao;
        this.deviceDao = deviceDao;
        this.locationDao = locationDao;
    }

    public final List<CompositeItem> getAll() {
        List<CompositeItem> compositeItems = compositeItemDao.findAll();
        log.info("Composite items fetched from db: {}", compositeItems);
        return compositeItems;
    }

    public final CompositeItem create(final CompositeItem compositeItemRequest) {
        checkLocation(compositeItemRequest);

        CompositeItem saved = compositeItemDao.save(compositeItemRequest);
        log.info("Composite item saved: {}", saved);
        return saved;
    }

    public final CompositeItem update(final CompositeItem compositeItemRequest) {
        checkLocation(compositeItemRequest);

        CompositeItem compositeItemToUpdate = compositeItemDao.findById(compositeItemRequest.getId()).orElse(null);

        if (compositeItemToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.setName(compositeItemRequest.getName());
        compositeItemToUpdate.setBarcode(compositeItemRequest.getBarcode());
        compositeItemToUpdate.setLocation(compositeItemRequest.getLocation());

        CompositeItem saved = compositeItemDao.save(compositeItemToUpdate);
        log.info("Saved composite item: {}", saved);
        return saved;
    }

    public final CompositeItem delete(final CompositeItem compositeItemRequest) {
        compositeItemDao.delete(compositeItemRequest);
        log.info("Composite item deleted: {}", compositeItemRequest);
        return compositeItemRequest;
    }

    public final CompositeItem getOne(final Long compositeId) {
        CompositeItem foundCompositeItem = compositeItemDao.findById(compositeId).orElse(null);

        if (foundCompositeItem == null) {
            throw new ObjectNotFoundException();
        }

        log.info("Fetched composite item from db: {}", foundCompositeItem);
        return foundCompositeItem;
    }

    public final CompositeItem addDevice(final Long compositeId, final Device deviceRequest) {
        CompositeItem compositeItemToUpdate = compositeItemDao.findById(compositeId).orElse(null);

        if (compositeItemToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.getDevices().add(deviceRequest);
        CompositeItem saved = compositeItemDao.save(compositeItemToUpdate);
        log.info("Device added to rent: {}", saved);
        return saved;
    }

    public final CompositeItem deleteDevice(final Long compositeId, final Device deviceRequest) {
        CompositeItem compositeItemToUpdate = compositeItemDao.findById(compositeId).orElse(null);
        Device deviceToRemove = deviceDao.findById(deviceRequest.getId()).orElse(null);

        if (compositeItemToUpdate == null || deviceToRemove == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.getDevices().remove(deviceToRemove);
        CompositeItem saved = compositeItemDao.save(compositeItemToUpdate);
        log.info("Device removed from rent: {}", saved);
        return saved;
    }

    private void checkLocation(final CompositeItem compositeRequest) {
        Location location = locationDao.findByName(compositeRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationDao.save(Location.builder()
                .withName(compositeRequest.getLocation().getName())
                .build());
        }
        compositeRequest.setLocation(location);
    }
}
