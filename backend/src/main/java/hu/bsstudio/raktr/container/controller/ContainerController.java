package hu.bsstudio.raktr.container.controller;

import hu.bsstudio.raktr.container.service.ContainerService;
import hu.bsstudio.raktr.dto.container.ContainerAddDevicesDto;
import hu.bsstudio.raktr.dto.container.ContainerCreateDto;
import hu.bsstudio.raktr.dto.container.ContainerDetailsDto;
import hu.bsstudio.raktr.dto.container.ContainerUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/container")
public class ContainerController {

    private final ContainerService containerService;

    @GetMapping
    public List<ContainerDetailsDto> listContainers(
            @RequestParam(required = false, defaultValue = "false") Boolean deleted
    ) {
        return containerService.listContainers(deleted);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContainerDetailsDto createContainer(@RequestBody @Valid ContainerCreateDto createDto) {
        return containerService.createContainer(createDto);
    }

    @GetMapping("/{containerId}")
    public ContainerDetailsDto getContainerById(@PathVariable Long containerId) {
        return containerService.getContainerById(containerId);
    }

    @PutMapping("/{containerId}")
    public ContainerDetailsDto updateContainer(
            @PathVariable Long containerId,
            @RequestBody @Valid ContainerUpdateDto updateDto
    ) {
        return containerService.updateContainer(containerId, updateDto);
    }

    @PostMapping("/{containerId}/devices")
    public ContainerDetailsDto addDevicesToContainer(
            @PathVariable Long containerId,
            @RequestBody @Valid ContainerAddDevicesDto addDevicesDto
    ) {
        return containerService.addDevicesToContainer(containerId, addDevicesDto);
    }

    @DeleteMapping("/{containerId}/devices/{deviceId}")
    public ContainerDetailsDto removeDeviceFromContainer(
            @PathVariable Long containerId,
            @PathVariable Long deviceId
    ) {
        return containerService.removeDeviceFromContainer(containerId, deviceId);
    }

}
