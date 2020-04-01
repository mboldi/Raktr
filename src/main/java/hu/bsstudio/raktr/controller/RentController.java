package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.DeviceRentItem;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.service.RentService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/rent")
public final class RentController {

    private final RentService rentService;

    public RentController(final RentService rentService) {
        this.rentService = rentService;
    }

    @GetMapping
    public List<Rent> getRentList() {
        log.info("Incoming request for all rents");
        return rentService.getAll();
    }

    @PostMapping
    public Rent createRent(@Valid @RequestBody final Rent rentRequest) {
        log.info("Incoming request for new rent: {}", rentRequest);
        return rentService.create(rentRequest);
    }

    @PutMapping("/{rentId}")
    public Rent addDeviceToRent(@PathVariable final Long rentId, @RequestBody @Valid final DeviceRentItem deviceRentItemRequest) {
        log.info("Incoming request to add device {} to rent with id {}", deviceRentItemRequest, rentId);
        return rentService.updateDeviceInRent(rentId, deviceRentItemRequest);
    }

    @GetMapping("/{rentId}")
    public Rent getRentById(@PathVariable final Long rentId) {
        log.info("Incoming request for rent with id {}", rentId);
        return rentService.getById(rentId);
    }

    @PutMapping
    public Rent updateRent(@RequestBody @Valid final Rent rentRequest) {
        log.info("Incoming request to update rent: {}", rentRequest);
        return rentService.update(rentRequest);
    }

    @DeleteMapping
    public Rent deleteRent(@RequestBody @Valid final Rent rentRequest) {
        log.info("Incoming request to delete rent {}", rentRequest);
        return rentService.delete(rentRequest);
    }

}
