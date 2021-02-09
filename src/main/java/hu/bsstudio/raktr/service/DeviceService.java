package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
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

    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public DeviceService(final DeviceRepository deviceRepository, final CategoryRepository categoryRepository, final LocationRepository locationRepository) {
        this.deviceRepository = deviceRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    public Device create(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        var saved = deviceRepository.save(deviceRequest);
        log.info("Device created: {}", deviceRequest);
        return saved;
    }

    public List<Device> getAll() {
        var fetched = deviceRepository.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Device delete(final Device deviceRequest) {
        deviceRepository.delete(deviceRequest);
        log.info("Deleted device from DB: {}", deviceRequest);
        return deviceRequest;
    }

    public Device update(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        Device deviceToUpdate = deviceRepository.findById(deviceRequest.getId()).orElse(null);

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

        Device saved = deviceRepository.save(deviceToUpdate);
        log.info("Device updated in DB: {}", saved);
        return saved;
    }

    public Device getById(final Long id) {
        Optional<Device> foundDevice = deviceRepository.findById(id);

        if (foundDevice.isEmpty()) {
            log.error("Device not found with id: {}", id);
            throw new ObjectNotFoundException();
        }

        log.info("Device with id {} found: {}", id, foundDevice.get());
        return foundDevice.get();
    }

    private void checkCategoryAndLocation(final Device deviceRequest) {
        Category category = categoryRepository.findByName(deviceRequest.getCategory().getName()).orElse(null);

        if (category == null) {
            category = categoryRepository.save(Category.builder()
                .withName(deviceRequest.getCategory().getName())
                .build());
        }
        deviceRequest.setCategory(category);

        Location location = locationRepository.findByName(deviceRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationRepository.save(Location.builder()
                .withName(deviceRequest.getLocation().getName())
                .build());
        }
        deviceRequest.setLocation(location);
    }

    public Device deleteById(final Long id) {
        Optional<Device> deviceToDelete = deviceRepository.findById(id);

        if (deviceToDelete.isEmpty()) {
            throw new ObjectNotFoundException();
        } else {
            deviceRepository.deleteById(id);
            log.info("Device by id deleted: {}", deviceToDelete.get());
            return deviceToDelete.get();
        }
    }
}
