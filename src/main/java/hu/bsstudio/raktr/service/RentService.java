package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.DeviceRentItemDao;
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
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

    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean checkIfAvailable(final DeviceRentItem deviceRentItem, final DeviceRentItem rentItemToUpdate) {
        Integer maxAvailableQuantity = deviceDao.getOne(deviceRentItem.getDevice().getId()).getQuantity();
        List<DeviceRentItem> deviceRentItems = deviceRentItemDao.findAll();
        Integer sumOut = 0;

        if (deviceRentItem.getOutQuantity() > maxAvailableQuantity) {
            return false;
        }

        for (DeviceRentItem rentItem : deviceRentItems) {
            if (rentItem.getBackStatus().equals(BackStatus.OUT)
                && rentItem.getDevice().getId().equals(deviceRentItem.getDevice().getId())
                && (rentItemToUpdate == null || !rentItem.getId().equals(rentItemToUpdate.getId()))) {
                sumOut += rentItem.getOutQuantity();
            }

            if (sumOut + deviceRentItem.getOutQuantity() > maxAvailableQuantity) {
                return false;
            }
        }

        return true;
    }

    public final Rent create(final Rent rentRequest) {
        Rent saved = rentDao.save(rentRequest);
        log.info("Rent saved: {}", saved);
        return saved;
    }

    public final Rent updateDeviceInRent(final Long rentId, final DeviceRentItem newDeviceRentItem) {
        Rent rentToUpdate = rentDao.findById(rentId).orElse(null);
        DeviceRentItem savedDeviceItem;
        DeviceRentItem rentItemToUpdate;

        if (rentToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        rentItemToUpdate = rentToUpdate.getRentItemOfDevice(newDeviceRentItem.getDevice());

        if (!checkIfAvailable(newDeviceRentItem, rentItemToUpdate)) {
            throw new NotAvailableQuantityException();
        }

        if (rentItemToUpdate != null) {
            if (newDeviceRentItem.getOutQuantity() == 0) {
                rentToUpdate.getRentItems().remove(rentItemToUpdate);
                deviceRentItemDao.delete(rentItemToUpdate);
            } else {
                rentItemToUpdate.setOutQuantity(newDeviceRentItem.getOutQuantity());
                rentItemToUpdate.setBackStatus(newDeviceRentItem.getBackStatus());

                deviceRentItemDao.save(rentItemToUpdate);
            }
        } else {
            if (newDeviceRentItem.getOutQuantity() != 0) {
                savedDeviceItem = deviceRentItemDao.save(newDeviceRentItem);
                rentToUpdate.getRentItems().add(savedDeviceItem);
            }
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

    public final Rent update(final Rent rentRequest) {
        Rent rentToUpdate = rentDao.findById(rentRequest.getId()).orElse(null);

        if (rentToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        rentToUpdate.setDestination(rentRequest.getDestination());
        rentToUpdate.setIssuer(rentRequest.getIssuer());
        rentToUpdate.setOutDate(rentRequest.getOutDate());
        rentToUpdate.setExpBackDate(rentRequest.getExpBackDate());
        rentToUpdate.setActBackDate(rentRequest.getActBackDate());

        Rent saved = rentDao.save(rentToUpdate);
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final Rent delete(final Rent rentRequest) {
        rentDao.delete(rentRequest);
        log.info("Rent deleted: {}", rentRequest);
        return rentRequest;
    }

    public final Rent getById(final Long rentId) {
        Rent foundRent = rentDao.findById(rentId).orElse(null);

        if (foundRent == null) {
            log.error("Rent not found with ID {}", rentId);
            throw new ObjectNotFoundException();
        }

        log.info("Rent found: {}", foundRent);
        return foundRent;
    }
}