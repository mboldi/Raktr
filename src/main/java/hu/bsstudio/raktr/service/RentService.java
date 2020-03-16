package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.DeviceRentItemDao;
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.model.BackStatus;
import hu.bsstudio.raktr.model.DeviceRentItem;
import hu.bsstudio.raktr.model.Rent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RentService {

    private final RentDao rentDao;
    private final DeviceRentItemDao deviceRentItemDao;
    private final DeviceDao deviceDao;

    public RentService(final RentDao rentDao, final DeviceRentItemDao deviceRentItemDao, final DeviceDao deviceDao) {
        this.rentDao = rentDao;
        this.deviceRentItemDao = deviceRentItemDao;
        this.deviceDao = deviceDao;
    }

    public final boolean checkIfAvailable(final DeviceRentItem device) {
        Integer maxAvailableQuantity = deviceDao.getOne(device.getId()).getQuantity();
        List<DeviceRentItem> deviceRentItems = deviceRentItemDao.findAll();
        Integer sumOut = 0;

        if (device.getOutQuantity() > maxAvailableQuantity) {
            return false;
        }

        for (DeviceRentItem rentItem : deviceRentItems) {
            if (rentItem.getBackStatus().equals(BackStatus.OUT) && rentItem.getDevice().getId().equals(device.getId())) {
                sumOut += rentItem.getOutQuantity();
                if (sumOut + device.getOutQuantity() > maxAvailableQuantity) {
                    return false;
                }
            }
        }

        return true;
    }

    public final Rent create(final Rent rentRequest) {
        Rent saved = rentDao.save(rentRequest);
        log.info("Rent saved: {}", saved);
        return saved;
    }

    public final Rent addDeviceToRent(final Long rentId, final DeviceRentItem newDevice) {
        Rent rentToUpdate = rentDao.getOne(rentId);
        DeviceRentItem savedDeviceItem;

        if (!checkIfAvailable(newDevice)) {
            throw new NotAvailableQuantityException();
        }

        if (rentToUpdate.getDevices().contains(newDevice)) {
            if (newDevice.getOutQuantity() == 0) {
                rentToUpdate.getDevices().remove(newDevice);
            } else {
                DeviceRentItem deviceRentItemToModify = deviceRentItemDao.getOne(newDevice.getId());
                deviceRentItemToModify.setOutQuantity(newDevice.getOutQuantity());
                deviceRentItemDao.save(deviceRentItemToModify);
            }
        } else {
            savedDeviceItem = deviceRentItemDao.save(newDevice);
            rentToUpdate.getDevices().add(savedDeviceItem);
        }

        Rent saved = rentDao.save(rentToUpdate);
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final List<Rent> getAll() {
        List<Rent> all = rentDao.findAll();
        log.info("Rents fetched from DB: {}", all);
        return all;
    }
}
