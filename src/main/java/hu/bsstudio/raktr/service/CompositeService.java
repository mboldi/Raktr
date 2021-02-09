package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
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

    private final CompositeItemRepository compositeItemRepository;
    private final DeviceRepository deviceRepository;
    private final LocationRepository locationRepository;

    public CompositeService(final CompositeItemRepository compositeItemRepository, final DeviceRepository deviceRepository, final LocationRepository locationRepository) {
        this.compositeItemRepository = compositeItemRepository;
        this.deviceRepository = deviceRepository;
        this.locationRepository = locationRepository;
    }

    public final List<CompositeItem> getAll() {
        List<CompositeItem> compositeItems = compositeItemRepository.findAll();
        log.info("Composite items fetched from db: {}", compositeItems);
        return compositeItems;
    }

    public final CompositeItem create(final CompositeItem compositeItemRequest) {
        checkLocation(compositeItemRequest);

        CompositeItem saved = compositeItemRepository.save(compositeItemRequest);
        log.info("Composite item saved: {}", saved);
        return saved;
    }

    public final CompositeItem update(final CompositeItem compositeItemRequest) {
        checkLocation(compositeItemRequest);

        CompositeItem compositeItemToUpdate = compositeItemRepository.findById(compositeItemRequest.getId()).orElse(null);

        if (compositeItemToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.setName(compositeItemRequest.getName());
        compositeItemToUpdate.setBarcode(compositeItemRequest.getBarcode());
        compositeItemToUpdate.setTextIdentifier(compositeItemRequest.getTextIdentifier());
        compositeItemToUpdate.setLocation(compositeItemRequest.getLocation());

        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate);
        log.info("Saved composite item: {}", saved);
        return saved;
    }

    public final CompositeItem delete(final CompositeItem compositeItemRequest) {
        compositeItemRepository.delete(compositeItemRequest);
        log.info("Composite item deleted: {}", compositeItemRequest);
        return compositeItemRequest;
    }

    public final CompositeItem getOne(final Long compositeId) {
        CompositeItem foundCompositeItem = compositeItemRepository.findById(compositeId).orElse(null);

        if (foundCompositeItem == null) {
            throw new ObjectNotFoundException();
        }

        log.info("Fetched composite item from db: {}", foundCompositeItem);
        return foundCompositeItem;
    }

    public final CompositeItem addDevice(final Long compositeId, final Device deviceRequest) {
        CompositeItem compositeItemToUpdate = compositeItemRepository.findById(compositeId).orElse(null);

        if (compositeItemToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.getDevices().add(deviceRequest);
        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate);
        log.info("Device added to composite item: {}", saved);
        return saved;
    }

    public final CompositeItem deleteDevice(final Long compositeId, final Device deviceRequest) {
        CompositeItem compositeItemToUpdate = compositeItemRepository.findById(compositeId).orElse(null);
        Device deviceToRemove = deviceRepository.findById(deviceRequest.getId()).orElse(null);

        if (compositeItemToUpdate == null || deviceToRemove == null) {
            throw new ObjectNotFoundException();
        }

        compositeItemToUpdate.getDevices().remove(deviceToRemove);
        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate);
        log.info("Device removed from composite item: {}", saved);
        return saved;
    }

    private void checkLocation(final CompositeItem compositeRequest) {
        Location location = locationRepository.findByName(compositeRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationRepository.save(Location.builder()
                .withName(compositeRequest.getLocation().getName())
                .build());
        }
        compositeRequest.setLocation(location);
    }
}
