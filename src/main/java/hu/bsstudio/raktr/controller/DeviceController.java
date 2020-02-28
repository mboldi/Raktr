package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.DeviceService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping()
    public final Device createDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request for new device: {}", deviceRequest);
        return deviceService.create(deviceRequest);
    }
}
