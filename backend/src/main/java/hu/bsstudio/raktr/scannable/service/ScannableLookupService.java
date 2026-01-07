package hu.bsstudio.raktr.scannable.service;

import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.dal.repository.CategoryRepository;
import hu.bsstudio.raktr.dal.repository.DeviceRepository;
import hu.bsstudio.raktr.dal.repository.LocationRepository;
import hu.bsstudio.raktr.dal.repository.OwnerRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScannableLookupService {

    private final CategoryRepository categoryRepository;

    private final DeviceRepository deviceRepository;

    private final LocationRepository locationRepository;

    private final OwnerRepository ownerRepository;

    public Category getCategory(String categoryName) {
        return categoryRepository.findById(categoryName).orElseThrow(ObjectNotFoundException::new);
    }

    public Device getDevice(Long deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(ObjectNotFoundException::new);
    }

    public Location getLocation(String locationName) {
        return locationRepository.findById(locationName).orElseThrow(ObjectNotFoundException::new);
    }

    public Owner getOwner(Integer ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(ObjectNotFoundException::new);
    }

}
