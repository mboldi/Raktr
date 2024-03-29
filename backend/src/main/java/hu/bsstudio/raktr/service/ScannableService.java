package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dto.RentItemWithRentData;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import hu.bsstudio.raktr.model.Ticket;
import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.RentItemRepository;
import hu.bsstudio.raktr.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScannableService {

    private final DeviceRepository deviceRepository;
    private final CompositeItemRepository compositeItemRepository;
    private final RentItemRepository rentItemRepository;

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

    public final Optional<List<RentItemWithRentData>> getRentItemsOfScannable(final Long id) {
        var foundDevice = deviceRepository.findById(id);

        if (foundDevice.isEmpty()) {
            var foundCompositeItem = compositeItemRepository.findById(id);
            if (foundCompositeItem.isEmpty()) {
                return Optional.empty();
            } else {
                //log.info("Composite Item found by id: {}", foundCompositeItem.get());
                return Optional.of(foundCompositeItem.get().getRentItems().stream()
                        .map(rentItem -> new RentItemWithRentData(rentItem, rentItem.getRent()))
                        .collect(Collectors.toList()));
            }
        } else {
            //log.info("Device found by barcode: {}", foundDevice.get());
            return Optional.of(foundDevice.get().getRentItems().stream()
                    .map(rentItem -> {
                        log.info("found: {}", rentItemRepository.findRentOfRentItem(rentItem.getId()).orElse(null));
                        return new RentItemWithRentData(rentItem, rentItemRepository.findRentOfRentItem(rentItem.getId()).orElse(null));
                    })
                    .collect(Collectors.toList()));
        }
    }

    public final Long getScannableCount() {
        return deviceRepository.count() + compositeItemRepository.count();
    }

    public final Long getNextId() {
        Long nextId = 1L;

        try {
            nextId = deviceRepository.getMaxId() + 1;
        } catch (Exception e) {

        }

        log.info("Found next id: {}", nextId);
        return nextId;
    }

    public final Long textIdTaken(final String textId) {
        Optional<Device> device = deviceRepository.findByTextIdentifier(textId);

        if (device.isPresent()) {
            return device.get().getId();
        }

        Optional<CompositeItem> compositeItem = compositeItemRepository.findByTextIdentifier(textId);

        if (compositeItem.isPresent()) {
            return compositeItem.get().getId();
        }

        return -1L;
    }

    public final Long barcodeTaken(final String barcode) {
        Optional<Device> device = deviceRepository.findByBarcode(barcode);

        if (device.isPresent()) {
            return device.get().getId();
        }

        Optional<CompositeItem> compositeItem = compositeItemRepository.findByBarcode(barcode);

        if (compositeItem.isPresent()) {
            return compositeItem.get().getId();
        }

        return -1L;
    }
}
