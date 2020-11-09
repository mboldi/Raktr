package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.service.LocationService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(final LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public Location createLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for new location: {}", locationRequest);
        return locationService.create(locationRequest);
    }

    @GetMapping
    public List<Location> locationList() {
        log.info("Incoming request for all locations");
        return locationService.getAll();
    }

    @PutMapping
    public Location updateLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for updating a location: {}", locationRequest);
        return locationService.update(locationRequest);
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public Location deleteLocation(@Valid @RequestBody final  Location locationRequest) {
        log.info("Incoming request to delete location: {}", locationRequest);
        return locationService.delete(locationRequest);
    }
}
