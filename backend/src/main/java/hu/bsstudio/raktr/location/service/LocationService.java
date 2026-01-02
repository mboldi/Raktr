package hu.bsstudio.raktr.location.service;

import hu.bsstudio.raktr.dal.repository.DeviceRepository;
import hu.bsstudio.raktr.dal.repository.LocationRepository;
import hu.bsstudio.raktr.dto.location.LocationCreateDto;
import hu.bsstudio.raktr.dto.location.LocationDetailsDto;
import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.location.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    private final DeviceRepository deviceRepository;

    private final LocationMapper locationMapper;

    public List<LocationDetailsDto> listLocations() {
        var locations = locationRepository.findAll();
        return locations.stream().map(locationMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public LocationDetailsDto createLocation(LocationCreateDto createDto) {
        if (locationRepository.existsById(createDto.getName())) {
            throw new ObjectConflictException();
        }

        createDto.setName(createDto.getName().trim());
        var location = locationMapper.createDtoToEntity(createDto);

        location = locationRepository.saveAndFlush(location);

        log.info("Created Location with name [{}]", location.getName());

        return locationMapper.entityToDetailsDto(location);
    }

    @Transactional
    public void deleteLocation(String locationName) {
        var location = locationRepository.findById(locationName)
                .orElseThrow(ObjectNotFoundException::new);

        if (deviceRepository.existsByLocation(location)) {
            throw new ObjectConflictException();
        }

        locationRepository.delete(location);

        log.info("Deleted Location with name [{}]", location.getName());
    }

}
