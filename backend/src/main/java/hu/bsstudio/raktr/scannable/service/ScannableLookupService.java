package hu.bsstudio.raktr.scannable.service;

import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dal.entity.Container;
import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.CategoryRepository;
import hu.bsstudio.raktr.dal.repository.ContainerRepository;
import hu.bsstudio.raktr.dal.repository.DeviceRepository;
import hu.bsstudio.raktr.dal.repository.LocationRepository;
import hu.bsstudio.raktr.dal.repository.OwnerRepository;
import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScannableLookupService {

    private final CategoryRepository categoryRepository;

    private final ContainerRepository containerRepository;

    private final DeviceRepository deviceRepository;

    private final LocationRepository locationRepository;

    private final OwnerRepository ownerRepository;

    private final ScannableRepository scannableRepository;

    public Category getCategory(String categoryName) {
        return categoryRepository.findById(categoryName)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryName));
    }

    public Container getContainer(Long containerId) {
        return containerRepository.findById(containerId)
                .orElseThrow(() -> new EntityNotFoundException(Container.class, containerId));
    }

    public Device getDevice(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException(Device.class, deviceId));
    }

    public Location getLocation(String locationName) {
        return locationRepository.findById(locationName)
                .orElseThrow(() -> new EntityNotFoundException(Location.class, locationName));
    }

    public Owner getOwner(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(Owner.class, ownerId));
    }

    public Scannable getScannable(Long scannableId) {
        return scannableRepository.findById(scannableId)
                .orElseThrow(() -> new EntityNotFoundException(Scannable.class, scannableId));
    }

}
