package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import java.util.Optional;
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

    public final Optional<? extends Scannable> getByBarcode(final String barcode) {
        var foundDevice = deviceRepository.findByBarcode(barcode);

        if (foundDevice.isEmpty()) {
            var foundCompositeItem = compositeItemRepository.findByBarcode(barcode);
            if (foundCompositeItem.isEmpty()) {
                return Optional.empty();
            } else {
                log.info("Composite Item found by barcode: {}", foundCompositeItem.get());
                return foundCompositeItem;
            }
        } else {
            log.info("Device found by barcode: {}", foundDevice.get());
            return foundDevice;
        }
    }

    public final Optional<? extends Scannable> getByTextIdentifier(final String textIdentifier) {
        var foundDevice = deviceRepository.findByTextIdentifier(textIdentifier);

        if (foundDevice.isEmpty()) {
            var foundCompositeItem = compositeItemRepository.findByTextIdentifier(textIdentifier);
            if (foundCompositeItem.isEmpty()) {
                return Optional.empty();
            } else {
                log.info("Composite Item found by textIdentifier: {}", foundCompositeItem.get());
                return foundCompositeItem;
            }
        } else {
            log.info("Device found by textIdentifier: {}", foundDevice.get());
            return foundDevice;
        }
    }
}
