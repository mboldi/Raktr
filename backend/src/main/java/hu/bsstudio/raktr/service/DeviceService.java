package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.model.Owner;
import hu.bsstudio.raktr.repository.CategoryRepository;
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
public final class DeviceService {

    private final OwnerService ownerService;
    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public Optional<Device> create(final Device deviceRequest) {
        var foundDevice = deviceRequest.getId() == null ? Optional.empty() : deviceRepository.findById(deviceRequest.getId());

        if (foundDevice.isEmpty()) {
            checkCategoryAndLocation(deviceRequest);

            deviceRequest.setIsDeleted(false);

            if (deviceRequest.getOwner() != null) {
                Optional<Owner> owner = ownerService.create(deviceRequest.getOwner());

                deviceRequest.setOwner(owner.get());
            }

            var saved = deviceRepository.save(deviceRequest);
            log.info("Device created: {}", deviceRequest);
            return Optional.of(saved);
        } else {
            log.info("Device failed to create, id conflict: {}", deviceRequest);
            return Optional.empty();
        }
    }

    public List<Device> getAll() {
        var fetched = deviceRepository.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public List<Device> getAllDeleted() {
        var fetched = deviceRepository.findAllDeleted();
        log.info("Deleted devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Optional<Device> delete(final Device deviceRequest) {
        var foundDevice = deviceRepository.findById(deviceRequest.getId());

        if (foundDevice.isPresent()) {
            foundDevice.get().setDeletedData();

            deviceRepository.save(deviceRequest);
            log.info("Deleted device from DB: {}", deviceRequest);
        } else {
            log.info("Device to delete not found in DB: {}", deviceRequest);
        }

        return foundDevice;
    }

    public Optional<Device> unDelete(final Device deviceRequest) {
        var foundDevice = deviceRepository.findById(deviceRequest.getId());

        if (foundDevice.isPresent()) {
            foundDevice.get().setUndeletedData();

            Optional<Device> byBarcode = deviceRepository.findByBarcode(foundDevice.get().getBarcode());
            Optional<Device> byTextIdentifier = deviceRepository.findByTextIdentifier(foundDevice.get().getTextIdentifier());

            if (byBarcode.isPresent() && !byBarcode.get().getId().equals(foundDevice.get().getId()) ||
                byTextIdentifier.isPresent() && !byTextIdentifier.get().getId().equals(foundDevice.get().getId())) {
                log.warn("Original device barcode ({}) or textID ({}) taken",
                    foundDevice.get().getBarcode(), foundDevice.get().getTextIdentifier());
                throw new ObjectConflictException();
            }

            deviceRepository.save(deviceRequest);
            log.info("Deleted device from DB: {}", deviceRequest);
        } else {
            log.warn("Device to delete not found in DB: {}", deviceRequest);
        }

        return foundDevice;
    }

    public Optional<Device> update(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        var deviceToUpdate = deviceRepository.findById(deviceRequest.getId());

        if (deviceToUpdate.isEmpty()) {
            log.warn("Device not found in db to update: {}", deviceRequest);
            return deviceToUpdate;
        }

        if (deviceRequest.getOwner() != null) {
            Optional<Owner> owner = ownerService.create(deviceRequest.getOwner());
            deviceToUpdate.get().setOwner(owner.get());
        }

        deviceToUpdate.get().setBarcode(deviceRequest.getBarcode());
        deviceToUpdate.get().setIsPublicRentable(deviceRequest.getIsPublicRentable());
        deviceToUpdate.get().setTextIdentifier(deviceRequest.getTextIdentifier());
        deviceToUpdate.get().setName(deviceRequest.getName());
        deviceToUpdate.get().setMaker(deviceRequest.getMaker());
        deviceToUpdate.get().setType(deviceRequest.getType());
        deviceToUpdate.get().setSerial(deviceRequest.getSerial());
        deviceToUpdate.get().setStatus(deviceRequest.getStatus());
        deviceToUpdate.get().setValue(deviceRequest.getValue());
        deviceToUpdate.get().setWeight(deviceRequest.getWeight());
        deviceToUpdate.get().setQuantity(deviceRequest.getQuantity());
        deviceToUpdate.get().setCategory(deviceRequest.getCategory());
        deviceToUpdate.get().setLocation(deviceRequest.getLocation());
        deviceToUpdate.get().setAquiredFrom(deviceRequest.getAquiredFrom());
        deviceToUpdate.get().setComment(deviceRequest.getComment());
        deviceToUpdate.get().setDateOfAcquisition(deviceRequest.getDateOfAcquisition());
        deviceToUpdate.get().setEndOfWarranty(deviceRequest.getEndOfWarranty());

        Device saved = deviceRepository.save(deviceToUpdate.get());
        log.info("Device updated in DB: {}", saved);
        return Optional.of(saved);
    }

    public Optional<Device> getById(final Long id) {
        Optional<Device> foundDevice = deviceRepository.findById(id);

        if (foundDevice.isEmpty()) {
            log.error("Device not found with id: {}", id);
            return foundDevice;
        }

        log.info("Device with id {} found: {}", id, foundDevice.get());
        return foundDevice;
    }

    private void checkCategoryAndLocation(final Device deviceRequest) {
        var category = categoryRepository.findByName(deviceRequest.getCategory().getName()).orElse(null);

        if (category == null) {
            category = categoryRepository.save(Category.builder()
                .name(deviceRequest.getCategory().getName())
                .build());
        }
        deviceRequest.setCategory(category);

        Location location = locationRepository.findByName(deviceRequest.getLocation().getName()).orElse(null);

        if (location == null) {
            location = locationRepository.save(Location.builder()
                .name(deviceRequest.getLocation().getName())
                .build());
        }
        deviceRequest.setLocation(location);
    }

    public Optional<Device> deleteById(final Long id) {
        Optional<Device> deviceToDelete = deviceRepository.findById(id);

        if (deviceToDelete.isEmpty()) {
            log.info("Device not found to delete with id: {}", id);
            return deviceToDelete;
        } else {
            deviceRepository.deleteById(id);
            log.info("Device by id deleted: {}", deviceToDelete.get());
            return deviceToDelete;
        }
    }
}
