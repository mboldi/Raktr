package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.DeviceService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request for new device: {}", deviceRequest);

        final var device = deviceService.create(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(409).build());
    }

    @GetMapping
    public List<Device> getDeviceList() {
        log.info("Incoming request for all Devices");
        return deviceService.getAll();
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<Device> deleteDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to delete device: {}", deviceRequest);

        final var device = deviceService.delete(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() ->  ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<Device> deleteDeviceById(@PathVariable final Long id) {
        log.info("Incoming request to delete device with id: {}", id);

        final var device = deviceService.deleteById(id);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Device> updateDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to update device: {}", deviceRequest);

        final var device = deviceService.update(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable final Long id) {
        log.info("Incoming request for device with id {}", id);
        final var device = deviceService.getById(id);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
