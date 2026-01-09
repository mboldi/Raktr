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
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
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
        return categoryRepository.findById(categoryName).orElseThrow(ObjectNotFoundException::new);
    }

    public Container getContainer(Long containerId) {
        return containerRepository.findById(containerId).orElseThrow(ObjectNotFoundException::new);
    }

    public Device getDevice(Long deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(ObjectNotFoundException::new);
    }

    public Location getLocation(String locationName) {
        return locationRepository.findById(locationName).orElseThrow(ObjectNotFoundException::new);
    }

    public Owner getOwner(Long ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(ObjectNotFoundException::new);
    }

    public Scannable getScannable(Long scannableId) {
        return scannableRepository.findById(scannableId).orElseThrow(ObjectNotFoundException::new);
    }

}
