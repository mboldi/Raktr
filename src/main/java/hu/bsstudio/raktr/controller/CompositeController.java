package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.CompositeService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/composite")
public class CompositeController {

    private final CompositeService compositeService;

    public CompositeController(final CompositeService compositeService) {
        this.compositeService = compositeService;
    }

    @GetMapping
    public List<CompositeItem> getCompositeList() {
        log.info("Incoming request for all composite items");
        return compositeService.getAll();
    }

    @PostMapping
    public CompositeItem createCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request for new composite item: {}", compositeItemRequest);
        return compositeService.create(compositeItemRequest);
    }

    @PutMapping
    public CompositeItem updateCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request to update composite item: {}", compositeItemRequest);
        return compositeService.update(compositeItemRequest);
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public CompositeItem deleteCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request to delete composite item: {}", compositeItemRequest);
        return compositeService.delete(compositeItemRequest);
    }

    @GetMapping("/{compositeId}")
    public CompositeItem getCompositeById(@PathVariable final Long compositeId) {
        log.info("Incoming request for composite item with id {}", compositeId);
        return compositeService.getOne(compositeId);
    }

    @PutMapping("/{compositeId}")
    public CompositeItem addDeviceToCompositeItem(@PathVariable final Long compositeId, @Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to add device {} to composite item with id {}", deviceRequest, compositeId);
        return compositeService.addDevice(compositeId, deviceRequest);
    }

    @DeleteMapping("/{compositeId}")
    public CompositeItem deleteDeviceFromCompositeItem(@PathVariable final Long compositeId, @Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to delete device {} from composite item with id {}", deviceRequest, compositeId);
        return compositeService.deleteDevice(compositeId, deviceRequest);
    }
}
