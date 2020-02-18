package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return deviceService.helloWorld();
    }

    @GetMapping("/helloWorld/{name}")
    public String helloWorld(@PathVariable("name") String name) {
        log.debug("asdasd");
        return deviceService.helloWorld(name);
    }
}
