package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.service.LocationService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(final LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public final Location createLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for new location: {}", locationRequest);
        return locationService.create(locationRequest);
    }

    @GetMapping
    public final List<Location> locationList() {
        log.info("Incoming request for all locations");
        return locationService.getAll();
    }

    @PutMapping
    public final Location updateLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for updating a location: {}", locationRequest);
        return locationService.update(locationRequest);
    }

    @DeleteMapping
    public final Location deleteLocation(@Valid @RequestBody final  Location locationRequest) {
        log.info("Incoming request to delete location: {}", locationRequest);
        return locationService.delete(locationRequest);
    }
}
