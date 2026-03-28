package hu.bsstudio.raktr.device.service;

import hu.bsstudio.raktr.dal.repository.DeviceRepository;
import hu.bsstudio.raktr.device.mapper.DeviceMapper;
import hu.bsstudio.raktr.dto.device.DeviceCreateDto;
import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import hu.bsstudio.raktr.dto.device.DeviceUpdateDto;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final ScannableLookupService lookupService;

    private final DeviceMapper deviceMapper;

    public List<DeviceDetailsDto> listDevices(boolean deleted) {
        var devices = deviceRepository.findAllByDeleted(deleted);
        return devices.stream().map(deviceMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public DeviceDetailsDto createDevice(DeviceCreateDto createDto) {
        var device = deviceMapper.createDtoToEntity(createDto);

        var category = lookupService.getCategory(createDto.getCategoryName());
        var location = lookupService.getLocation(createDto.getLocationName());
        var owner = lookupService.getOwner(createDto.getOwnerId());

        device.setCategory(category);
        device.setLocation(location);
        device.setOwner(owner);

        device = deviceRepository.saveAndFlush(device);

        log.info("Created Device with ID [{}]", device.getId());

        return deviceMapper.entityToDetailsDto(device);
    }

    public DeviceDetailsDto getDeviceById(Long deviceId) {
        var device = lookupService.getDevice(deviceId);
        return deviceMapper.entityToDetailsDto(device);
    }

    @Transactional
    public DeviceDetailsDto updateDevice(Long deviceId, DeviceUpdateDto updateDto) {
        var device = lookupService.getDevice(deviceId);

        deviceMapper.updateDtoToEntity(device, updateDto);

        var category = lookupService.getCategory(updateDto.getCategoryName());
        var location = lookupService.getLocation(updateDto.getLocationName());
        var owner = lookupService.getOwner(updateDto.getOwnerId());

        device.setCategory(category);
        device.setLocation(location);
        device.setOwner(owner);

        deviceRepository.saveAndFlush(device);

        log.info("Updated Device with ID [{}]", deviceId);

        return deviceMapper.entityToDetailsDto(device);
    }

    public List<String> getDistinctManufacturers() {
        return deviceRepository.findDistinctManufacturers();
    }

}
