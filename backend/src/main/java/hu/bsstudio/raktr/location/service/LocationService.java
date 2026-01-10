package hu.bsstudio.raktr.location.service;

import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.LocationRepository;
import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.dto.location.LocationCreateDto;
import hu.bsstudio.raktr.dto.location.LocationDetailsDto;
import hu.bsstudio.raktr.exception.EntityAlreadyExistsException;
import hu.bsstudio.raktr.exception.EntityInUseException;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
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

    private final ScannableRepository scannableRepository;

    private final LocationMapper locationMapper;

    public List<LocationDetailsDto> listLocations() {
        var locations = locationRepository.findAll();
        return locations.stream().map(locationMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public LocationDetailsDto createLocation(LocationCreateDto createDto) {
        createDto.setName(createDto.getName().trim());

        if (locationRepository.existsById(createDto.getName())) {
            throw new EntityAlreadyExistsException(Location.class, createDto.getName());
        }

        var location = locationMapper.createDtoToEntity(createDto);

        location = locationRepository.saveAndFlush(location);

        log.info("Created Location with name [{}]", location.getName());

        return locationMapper.entityToDetailsDto(location);
    }

    @Transactional
    public void deleteLocation(String locationName) {
        var location = locationRepository.findById(locationName)
                .orElseThrow(() -> new EntityNotFoundException(Location.class, locationName));

        if (scannableRepository.existsByLocation(location)) {
            throw new EntityInUseException(Location.class, locationName, Scannable.class);
        }

        locationRepository.delete(location);

        log.info("Deleted Location with name [{}]", location.getName());
    }

}
