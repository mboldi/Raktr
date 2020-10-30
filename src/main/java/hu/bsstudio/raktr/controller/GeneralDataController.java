package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.GeneralData;
import hu.bsstudio.raktr.service.GeneralDataService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
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
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/generalData")
public class GeneralDataController {
    private final GeneralDataService generalDataService;

    public GeneralDataController(final GeneralDataService generalDataService) {
        this.generalDataService = generalDataService;
    }

    @PostMapping
    public GeneralData createGeneralData(@Valid @RequestBody final GeneralData dataToAdd) {
        log.info("Incoming request to add General data: {}", dataToAdd);
        return generalDataService.create(dataToAdd);
    }

    @GetMapping
    public List<GeneralData> getAllGeneralData() {
        log.info("Incoming request for all general data");
        return generalDataService.getAll();
    }

    @GetMapping("/{key}")
    public GeneralData getByKey(@PathVariable final String key) {
        log.info("Incoming request for General data with key: {}", key);
        return generalDataService.getByKey(key);
    }

    @PutMapping
    public GeneralData updateData(@Valid @RequestBody final GeneralData dataToUpdate) {
        log.info("Incoming request to update data: {}", dataToUpdate);
        return generalDataService.update(dataToUpdate);
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public GeneralData deleteData(@Valid @RequestBody final GeneralData dataToDelete) {
        log.info("Incoming request to delete general data: {}", dataToDelete);
        return generalDataService.delete(dataToDelete);
    }
}
