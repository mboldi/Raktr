package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.CompositeItemDao;
import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScannableService {

    private final DeviceDao deviceDao;
    private final CompositeItemDao compositeItemDao;

    public ScannableService(final DeviceDao deviceDao, final CompositeItemDao compositeItemDao) {
        this.deviceDao = deviceDao;
        this.compositeItemDao = compositeItemDao;
    }

    public final Scannable getByBarcode(final String barcode) {
        Device foundDevice = deviceDao.findByBarcode(barcode).orElse(null);

        if (foundDevice == null) {
            CompositeItem foundCompositeItem = compositeItemDao.findByBarcode(barcode).orElse(null);
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
