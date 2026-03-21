package hu.bsstudio.raktr.container.service;

import hu.bsstudio.raktr.container.mapper.ContainerMapper;
import hu.bsstudio.raktr.dal.entity.ContainerItem;
import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.repository.ContainerRepository;
import hu.bsstudio.raktr.dto.container.ContainerAddDevicesDto;
import hu.bsstudio.raktr.dto.container.ContainerCreateDto;
import hu.bsstudio.raktr.dto.container.ContainerDetailsDto;
import hu.bsstudio.raktr.dto.container.ContainerItemUpdateDto;
import hu.bsstudio.raktr.dto.container.ContainerUpdateDto;
import hu.bsstudio.raktr.exception.EntityAlreadyExistsException;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;

    private final ScannableLookupService lookupService;

    private final ContainerMapper containerMapper;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

        for (var item : addDevicesDto.getItems()) {
            var existingItem = container.getItems().stream()
                    .filter(ci -> ci.getDevice().getId().equals(item.getDeviceId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                throw new EntityAlreadyExistsException(ContainerItem.class, item.getDeviceId());
            }

            var device = lookupService.getDevice(item.getDeviceId());

            var containerItem = new ContainerItem();
            containerItem.setContainer(container);
            containerItem.setDevice(device);
            containerItem.setQuantity(item.getQuantity());

            container.getItems().add(containerItem);
        }

        containerRepository.saveAndFlush(container);

        log.info("Devices added to Container [{}]", containerId);

        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto updateDeviceInContainer(Long containerId, Long deviceId, ContainerItemUpdateDto updateDto) {
        var container = lookupService.getContainer(containerId);
        var item = container.getItems().stream()
                .filter(ci -> ci.getDevice().getId().equals(deviceId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Device.class, deviceId));

        item.setQuantity(updateDto.getQuantity());

        containerRepository.saveAndFlush(container);

        log.info("Updated Device [{}] quantity in Container [{}]", deviceId, containerId);

        return containerMapper.entityToDetailsDto(container);
    }

    @Transactional
    public ContainerDetailsDto removeDeviceFromContainer(Long containerId, Long deviceId) {
        var container = lookupService.getContainer(containerId);
        var itemToRemove = container.getItems().stream()
                .filter(item -> item.getDevice().getId().equals(deviceId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Device.class, deviceId));

        container.getItems().remove(itemToRemove);

        containerRepository.saveAndFlush(container);

        log.info("Removed Device [{}] from Container [{}]", deviceId, containerId);

        return containerMapper.entityToDetailsDto(container);
    }

}
