package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.GeneralData;
import hu.bsstudio.raktr.service.GeneralDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/generaldata")
@RequiredArgsConstructor
public class GeneralDataController {

    private static final int CONFLICT = 409;

    private final GeneralDataService generalDataService;

    @PostMapping
    public ResponseEntity<GeneralData> createGeneralData(@Valid @RequestBody final GeneralData dataToAdd) {
        log.info("Incoming request to add General data: {}", dataToAdd);

        final var generalData = generalDataService.create(dataToAdd);
        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @GetMapping
    public ResponseEntity<List<GeneralData>> getAllGeneralData() {
        log.info("Incoming request for all general data");
        return ResponseEntity.ok(generalDataService.getAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<GeneralData> getByKey(@PathVariable final String key) {
        log.info("Incoming request for General data with key: {}", key);

        final var generalData = generalDataService.getByKey(key);

        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<GeneralData> updateData(@Valid @RequestBody final GeneralData dataToUpdate) {
        log.info("Incoming request to update data: {}", dataToUpdate);
        return ResponseEntity.ok(generalDataService.update(dataToUpdate));
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<GeneralData> deleteData(@Valid @RequestBody final GeneralData dataToDelete) {
        log.info("Incoming request to delete general data: {}", dataToDelete);

        final var generalData = generalDataService.delete(dataToDelete);

        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
