package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompositeService {

    private final CompositeItemRepository compositeItemRepository;
    private final DeviceRepository deviceRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public final List<CompositeItem> getAll() {
        List<CompositeItem> compositeItems = compositeItemRepository.findAll();
        log.info("Composite items fetched from db: {}", compositeItems);
        return compositeItems;
    }

    public final List<CompositeItem> getAllDeleted() {
        List<CompositeItem> compositeItems = compositeItemRepository.findAllDeleted();
        log.info("Deleted composite items fetched from db: {}", compositeItems);
        return compositeItems;
    }

    public final Optional<CompositeItem> create(final CompositeItem compositeItemRequest) {
        if (compositeItemRequest.getId() != null) {
            final var compositeItem = compositeItemRepository.findById(compositeItemRequest.getId());

            if (compositeItem.isPresent()) {
                log.info("Couldn't create composite item, given id is used: {}", compositeItemRequest);
                return Optional.empty();
            }
        }

        checkCategoryAndLocation(compositeItemRequest);
        compositeItemRequest.setIsDeleted(false);

        CompositeItem saved = compositeItemRepository.save(compositeItemRequest);
        log.info("Composite item saved: {}", saved);
        return Optional.of(saved);
    }

    public final Optional<CompositeItem> update(final CompositeItem compositeItemRequest) {
        checkCategoryAndLocation(compositeItemRequest);

        var compositeItemToUpdate = compositeItemRepository.findById(compositeItemRequest.getId());

        if (compositeItemToUpdate.isEmpty()) {
            log.info("Composite item not found with given id: {}", compositeItemRequest);
            return Optional.empty();
        }

        compositeItemToUpdate.get().setName(compositeItemRequest.getName());
        compositeItemToUpdate.get().setBarcode(compositeItemRequest.getBarcode());
        compositeItemToUpdate.get().setIsPublicRentable(compositeItemRequest.getIsPublicRentable());
        compositeItemToUpdate.get().setTextIdentifier(compositeItemRequest.getTextIdentifier());
        compositeItemToUpdate.get().setLocation(compositeItemRequest.getLocation());
        compositeItemToUpdate.get().setCategory(compositeItemRequest.getCategory());

        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate.get());
        log.info("Saved composite item: {}", saved);
        return Optional.of(saved);
    }

    public final Optional<CompositeItem> delete(final CompositeItem compositeItemRequest) {
        final var composite = compositeItemRepository.findById(compositeItemRequest.getId());

        if (composite.isPresent()) {
            compositeItemRepository.delete(compositeItemRequest);
            log.info("Composite item deleted: {}", compositeItemRequest);
        } else {
            log.info("Composite item not found to delete: {}", compositeItemRequest);
        }

        return composite;
    }

    public final Optional<CompositeItem> unDelete(final CompositeItem compositeItemRequest) {
        var foundCompositeItem = compositeItemRepository.findById(compositeItemRequest.getId());

        if (foundCompositeItem.isPresent()) {
            foundCompositeItem.get().setUndeletedData();

            var byBarcode = compositeItemRepository.findByBarcode(foundCompositeItem.get().getBarcode());
            var byTextIdentifier = compositeItemRepository.findByTextIdentifier(foundCompositeItem.get().getTextIdentifier());

            if (byBarcode.isPresent() && !byBarcode.get().getId().equals(foundCompositeItem.get().getId()) ||
                byTextIdentifier.isPresent() && !byTextIdentifier.get().getId().equals(foundCompositeItem.get().getId())) {
                log.info("Original composite item barcode ({}) or textID ({}) taken",
                    foundCompositeItem.get().getBarcode(), foundCompositeItem.get().getTextIdentifier());
                throw new ObjectConflictException();
            }

            compositeItemRepository.save(compositeItemRequest);
            log.info("Restored composite item: {}", compositeItemRequest);
        } else {
            log.info("Composite item to restore not found: {}", compositeItemRequest);
        }

        return foundCompositeItem;
    }

    public final Optional<CompositeItem> getById(final Long compositeId) {
        var foundCompositeItem = compositeItemRepository.findById(compositeId);

        if (foundCompositeItem.isEmpty()) {
            log.info("Composite item not found by id: {}", compositeId);
            return foundCompositeItem;
        }

        log.info("Fetched composite item from db: {}", foundCompositeItem);
        return foundCompositeItem;
    }

    public final Optional<CompositeItem> addDevice(final Long compositeId, final Device deviceRequest) {
        var compositeItemToUpdate = compositeItemRepository.findById(compositeId);
        var deviceToAdd = deviceRepository.findById(deviceRequest.getId());

        if (compositeItemToUpdate.isEmpty() || deviceToAdd.isEmpty()) {
            return Optional.empty();
        }

        compositeItemToUpdate.get().getDevices().add(deviceRequest);
        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate.get());
        log.info("Device added to composite item: {}", saved);

        return Optional.of(saved);
    }

    public final Optional<CompositeItem> removeDeviceFromComposite(final Long compositeId, final Device deviceRequest) {
        var compositeItemToUpdate = compositeItemRepository.findById(compositeId);

        if (compositeItemToUpdate.isEmpty()) {
            return compositeItemToUpdate;
        }

        var deviceToRemove = findInComposite(compositeItemToUpdate.get(), deviceRequest);

        if (deviceToRemove.isEmpty()) {
            return Optional.empty();
        }

        compositeItemToUpdate.get().getDevices().remove(deviceToRemove.get());
        CompositeItem saved = compositeItemRepository.save(compositeItemToUpdate.get());
        log.info("Device removed from composite item: {}", saved);
        return Optional.of(saved);
    }

    private Optional<Device> findInComposite(final CompositeItem compositeItem, final Device device) {

        for (var deviceInComposite : compositeItem.getDevices()) {
            if (device.getId().equals(deviceInComposite.getId())) {
                return Optional.of(deviceInComposite);
            }
        }

        return Optional.empty();
    }

    private void checkCategoryAndLocation(final CompositeItem compositeItemRequest) {
        var category = categoryRepository.findByName(compositeItemRequest.getCategory().getName()).orElse(null);

        if (category == null) {
            category = categoryRepository.save(Category.builder()
                    .name(compositeItemRequest.getCategory().getName())
                    .build());
        }
        compositeItemRequest.setCategory(category);

        Location location = locationRepository.findByName(compositeItemRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationRepository.save(Location.builder()
                    .name(compositeItemRequest.getLocation().getName())
                    .build());
        }
        compositeItemRequest.setLocation(location);
    }
}
