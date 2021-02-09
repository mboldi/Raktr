package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScannableService {

    private final DeviceRepository deviceRepository;
    private final CompositeItemRepository compositeItemRepository;

    public ScannableService(final DeviceRepository deviceRepository, final CompositeItemRepository compositeItemRepository) {
        this.deviceRepository = deviceRepository;
        this.compositeItemRepository = compositeItemRepository;
    }

    public final Scannable getByBarcode(final String barcode) {
        Device foundDevice = deviceRepository.findByBarcode(barcode).orElse(null);

        if (foundDevice == null) {
            CompositeItem foundCompositeItem = compositeItemRepository.findByBarcode(barcode).orElse(null);
            if (foundCompositeItem == null) {
                throw new ObjectNotFoundException();
            } else {
                log.info("Composite Item found by barcode: {}", foundCompositeItem);
                return foundCompositeItem;
            }
        } else {
            log.info("Device found by barcode: {}", foundDevice);
            return foundDevice;
        }
    }
}
