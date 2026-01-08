package hu.bsstudio.raktr.container.service;

import hu.bsstudio.raktr.container.mapper.ContainerMapper;
import hu.bsstudio.raktr.dal.repository.ContainerRepository;
import hu.bsstudio.raktr.dto.container.ContainerAddDevicesDto;
import hu.bsstudio.raktr.dto.container.ContainerCreateDto;
import hu.bsstudio.raktr.dto.container.ContainerDetailsDto;
import hu.bsstudio.raktr.dto.container.ContainerUpdateDto;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;

    private final ScannableLookupService lookupService;

    private final ContainerMapper containerMapper;

    @Transactional
    public List<ContainerDetailsDto> listContainers(boolean deleted) {
        var containers = containerRepository.findAllByDeleted(deleted);
        return containers.stream().map(containerMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public ContainerDetailsDto createContainer(ContainerCreateDto createDto) {
        var container = containerMapper.createDtoToEntity(createDto);

        var category = lookupService.getCategory(createDto.getCategoryName());
        var location = lookupService.getLocation(createDto.getLocationName());
        var owner = lookupService.getOwner(createDto.getOwnerId());

        container.setCategory(category);
        container.setLocation(location);
        container.setOwner(owner);

        container = containerRepository.saveAndFlush(container);

        log.info("Created Container with ID [{}]", container.getId());

        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto getContainerById(Long containerId) {
        var container = lookupService.getContainer(containerId);
        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto updateContainer(Long containerId, ContainerUpdateDto updateDto) {
        var container = lookupService.getContainer(containerId);

        containerMapper.updateDtoToEntity(container, updateDto);

        var category = lookupService.getCategory(updateDto.getCategoryName());
        var location = lookupService.getLocation(updateDto.getLocationName());
        var owner = lookupService.getOwner(updateDto.getOwnerId());

        container.setCategory(category);
        container.setLocation(location);
        container.setOwner(owner);

        containerRepository.saveAndFlush(container);

        log.info("Updated Container with ID [{}]", containerId);

        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto addDevicesToContainer(Long containerId, ContainerAddDevicesDto addDevicesDto) {
        var container = lookupService.getContainer(containerId);

        var devices = addDevicesDto.getDeviceIds().stream()
                .map(lookupService::getDevice)
                .toList();

        container.getDevices().addAll(devices);

        containerRepository.saveAndFlush(container);

        log.info("Devices added to Container [{}]: [{}]", containerId, addDevicesDto.getDeviceIds());

        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto removeDeviceFromContainer(Long containerId, Long deviceId) {
        var container = lookupService.getContainer(containerId);
        var deviceToRemove = container.getDevices().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findFirst()
                .orElseThrow(ObjectNotFoundException::new);

        container.getDevices().remove(deviceToRemove);

        containerRepository.saveAndFlush(container);

        log.info("Removed Device [{}] from Container [{}]", deviceId, containerId);

        return containerMapper.entityToDetailsDto(container);
    }

}
