package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.CategoryDao;
import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class DeviceService {

    private final DeviceDao deviceDao;
    private final CategoryDao categoryDao;
    private final LocationDao locationDao;

    public DeviceService(final DeviceDao deviceDao, final CategoryDao categoryDao, final LocationDao locationDao) {
        this.deviceDao = deviceDao;
        this.categoryDao = categoryDao;
        this.locationDao = locationDao;
    }

    public Device create(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        var saved = deviceDao.save(deviceRequest);
        log.info("Device created: {}", deviceRequest);
        return saved;
    }

    public List<Device> getAll() {
        var fetched = deviceDao.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Device delete(final Device deviceRequest) {
        deviceDao.delete(deviceRequest);
        log.info("Deleted device from DB: {}", deviceRequest);
        return deviceRequest;
    }

    public Device update(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        Device deviceToUpdate = deviceDao.findById(deviceRequest.getId()).orElse(null);

        if (deviceToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        deviceToUpdate.setBarcode(deviceRequest.getBarcode());
        deviceToUpdate.setName(deviceRequest.getName());
        deviceToUpdate.setMaker(deviceRequest.getMaker());
        deviceToUpdate.setType(deviceRequest.getType());
        deviceToUpdate.setSerial(deviceRequest.getSerial());
        deviceToUpdate.setStatus(deviceRequest.getStatus());
        deviceToUpdate.setValue(deviceRequest.getValue());
        deviceToUpdate.setWeight(deviceRequest.getWeight());
        deviceToUpdate.setQuantity(deviceRequest.getQuantity());
        deviceToUpdate.setCategory(deviceRequest.getCategory());
        deviceToUpdate.setLocation(deviceRequest.getLocation());

        Device saved = deviceDao.save(deviceToUpdate);
        log.info("Device updated in DB: {}", saved);
        return saved;
    }

    public Device getById(final Long id) {
        Optional<Device> foundDevice = deviceDao.findById(id);

        if (foundDevice.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        log.info("Device with id {} found: {}", id, foundDevice.get());
        return foundDevice.get();
    }

    private void checkCategoryAndLocation(final Device deviceRequest) {
        Category category = categoryDao.findByName(deviceRequest.getCategory().getName()).orElse(null);

        if (category == null) {
            category = categoryDao.save(Category.builder()
                .withName(deviceRequest.getCategory().getName())
                .build());
        }
        deviceRequest.setCategory(category);

        Location location = locationDao.findByName(deviceRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationDao.save(Location.builder()
                .withName(deviceRequest.getLocation().getName())
                .build());
        }
        deviceRequest.setLocation(location);
    }
}
