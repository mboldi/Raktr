package hu.bsstudio.raktr.location.controller;

import hu.bsstudio.raktr.dto.location.LocationCreateDto;
import hu.bsstudio.raktr.dto.location.LocationDetailsDto;
import hu.bsstudio.raktr.location.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Locations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public List<LocationDetailsDto> listLocations() {
        return locationService.listLocations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDetailsDto createLocation(@RequestBody @Valid LocationCreateDto createDto) {
        return locationService.createLocation(createDto);
    }

    @DeleteMapping("/{locationName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable String locationName) {
        locationService.deleteLocation(locationName);
    }

}
