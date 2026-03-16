package hu.bsstudio.raktr.device.controller;

import hu.bsstudio.raktr.device.service.DeviceService;
import hu.bsstudio.raktr.dto.device.DeviceCreateDto;
import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import hu.bsstudio.raktr.dto.device.DeviceUpdateDto;
import hu.bsstudio.raktr.dto.rent.RentDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketDetailsDto;
import hu.bsstudio.raktr.rent.service.RentService;
import hu.bsstudio.raktr.scannable.service.ScannableService;
import hu.bsstudio.raktr.security.RoleConstants;
import hu.bsstudio.raktr.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Devices")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    private final ScannableService scannableService;

    private final RentService rentService;

    private final TicketService ticketService;

    @GetMapping
    public List<DeviceDetailsDto> listDevices(
            @RequestParam(required = false, defaultValue = "false") boolean deleted
    ) {
        return deviceService.listDevices(deleted);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceDetailsDto createDevice(@RequestBody @Valid DeviceCreateDto createDto) {
        return deviceService.createDevice(createDto);
    }

    @GetMapping("/{deviceId}")
    public DeviceDetailsDto getDeviceById(@PathVariable Long deviceId) {
        return deviceService.getDeviceById(deviceId);
    }

    @PutMapping("/{deviceId}")
    public DeviceDetailsDto updateDevice(
            @PathVariable Long deviceId,
            @RequestBody @Valid DeviceUpdateDto updateDto
    ) {
        return deviceService.updateDevice(deviceId, updateDto);
    }

    @Secured(RoleConstants.MEMBER)
    @DeleteMapping("/{deviceId}")
    public void deleteDevice(@PathVariable Long deviceId) {
        scannableService.deleteScannable(deviceId);
    }

    @Secured(RoleConstants.ADMIN)
    @PostMapping("/{deviceId}/restore")
    public void restoreDevice(@PathVariable Long deviceId) {
        scannableService.restoreScannable(deviceId);
    }

    @GetMapping("/{deviceId}/rents")
    public List<RentDetailsDto> getRentsForDevice(@PathVariable Long deviceId) {
        return rentService.getRentsByScannableId(deviceId);
    }

    @GetMapping("/{deviceId}/tickets")
    public List<TicketDetailsDto> getTicketsForDevice(@PathVariable Long deviceId) {
        return ticketService.getTicketsByScannableId(deviceId);
    }

    @GetMapping("/manufacturers")
    public List<String> listManufacturers() {
        return deviceService.getDistinctManufacturers();
    }

}
