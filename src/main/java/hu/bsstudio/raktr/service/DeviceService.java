package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Device;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class DeviceService {

    private final DeviceDao deviceDao;

    public DeviceService(final DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public Device create(final Device device) {
        var saved = deviceDao.save(device);
        log.info("Device created: {}", device);
        return saved;
    }

    public List<Device> getAll() {
        var fetched = deviceDao.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Device delete(final Device deviceRequest) {
        deviceDao.delete(deviceRequest);
        log.info("Deleted device from DB: {}", deviceRequest);
        return deviceRequest;
    }

    public Device update(final Device deviceRequest) {
        Device deviceToUpdate;
        deviceToUpdate = deviceDao.findById(deviceRequest.getId()).orElse(null);

        if (deviceToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        deviceToUpdate.setBarcode(deviceRequest.getBarcode());
        deviceToUpdate.setName(deviceRequest.getName());
        deviceToUpdate.setMaker(deviceRequest.getMaker());
        deviceToUpdate.setType(deviceRequest.getType());
        deviceToUpdate.setSerial(deviceRequest.getSerial());
        deviceToUpdate.setStatus(deviceRequest.getStatus());
        deviceToUpdate.setValue(deviceRequest.getValue());
        deviceToUpdate.setWeight(deviceRequest.getWeight());
        deviceToUpdate.setQuantity(deviceRequest.getQuantity());

        Device saved = deviceDao.save(deviceToUpdate);
        log.info("Device updated in DB: {}", saved);
        return saved;
    }

    public Device getById(final Long id) {
        Device foundDevice;
        try {
            foundDevice = deviceDao.getOne(id);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new ObjectNotFoundException();
        }

        log.info("Device with id {} found: {}", id, foundDevice);
        return foundDevice;
    }

    public Device getByBarcode(final String barcode) {
        Device foundDevice = deviceDao.findByBarcode(barcode);
        if (foundDevice == null) {
            throw new ObjectNotFoundException();
        }

        log.info("Device with barcode {} found: {}", barcode, foundDevice);
        return foundDevice;
    }
}
