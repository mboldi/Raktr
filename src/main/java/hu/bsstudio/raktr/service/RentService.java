package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.dao.RentItemDao;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.BackStatus;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RentService {

    private final RentDao rentDao;
    private final RentItemDao rentItemDao;
    private final DeviceDao deviceDao;

    public RentService(final RentDao rentDao, final RentItemDao rentItemDao, final DeviceDao deviceDao) {
        this.rentDao = rentDao;
        this.rentItemDao = rentItemDao;
        this.deviceDao = deviceDao;
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean checkIfAvailable(final RentItem deviceRentItem, final RentItem rentItemToUpdate) {
        Integer maxAvailableQuantity = deviceDao.getOne(deviceRentItem.getScannable().getId()).getQuantity();
        List<RentItem> rentItems = rentItemDao.findAll();
        Integer sumOut = 0;

        if (deviceRentItem.getOutQuantity() > maxAvailableQuantity) {
            return false;
        }

        for (RentItem rentItem : rentItems) {
            if (rentItem.getBackStatus().equals(BackStatus.OUT)
                && rentItem.getScannable().getId().equals(deviceRentItem.getScannable().getId())
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

    public final Rent updateItem(final Long rentId, final RentItem newRentItem) {
        Rent rentToUpdate = rentDao.findById(rentId).orElse(null);
        RentItem savedDeviceItem;
        RentItem rentItemToUpdate;

        if (rentToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        rentItemToUpdate = rentToUpdate.getRentItemOfScannable(newRentItem.getScannable());

        if (newRentItem.getScannable().getClass() == Device.class && !checkIfAvailable(newRentItem, rentItemToUpdate)) {
            throw new NotAvailableQuantityException();
        }

        if (rentItemToUpdate != null) {
            if (newRentItem.getOutQuantity() == 0) {
                rentToUpdate.getRentItems().remove(rentItemToUpdate);
                rentItemDao.delete(rentItemToUpdate);
            } else {
                rentItemToUpdate.setOutQuantity(newRentItem.getOutQuantity());
                rentItemToUpdate.setBackStatus(newRentItem.getBackStatus());

                rentItemDao.save(rentItemToUpdate);
            }
        } else {
            if (newRentItem.getOutQuantity() != 0) {
                savedDeviceItem = rentItemDao.save(newRentItem);
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
