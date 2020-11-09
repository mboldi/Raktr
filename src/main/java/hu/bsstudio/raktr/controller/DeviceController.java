package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.DeviceService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public Device createDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request for new device: {}", deviceRequest);
        return deviceService.create(deviceRequest);
    }

    @GetMapping
    public List<Device> getDeviceList() {
        log.info("Incoming request for all Devices");
        return deviceService.getAll();
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public Device deleteDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to delete device: {}", deviceRequest);
        return deviceService.delete(deviceRequest);
    }

    @PutMapping
    public Device updateDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to update device: {}", deviceRequest);
        return deviceService.update(deviceRequest);
    }

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable final Long id) {
        log.info("Incoming request for device with id {}", id);
        return deviceService.getById(id);
    }
}
