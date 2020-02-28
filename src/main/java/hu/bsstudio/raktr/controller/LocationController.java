package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
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
}
