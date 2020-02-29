package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.model.Device;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class DeviceService {

    private final DeviceDao deviceDao;

    public DeviceService(final DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public Device create(final Device device) {
        final Device saved = deviceDao.save(device);
        log.info("Device created: {}", device);
        return saved;
    }

    public List<Device> getAll() {
        var fetched = deviceDao.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Device delete(final Device deviceRequest) {
        deviceDao.deleteById(deviceRequest.getId());
        log.info("Deleted device from DB: {}", deviceRequest);
        return deviceRequest;
    }
}
