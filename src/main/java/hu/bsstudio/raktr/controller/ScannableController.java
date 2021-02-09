package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Scannable;
import hu.bsstudio.raktr.service.ScannableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerResponse;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/scannable")
public class ScannableController {

    private final ScannableService scannableService;

    public ScannableController(final ScannableService scannableService) {
        this.scannableService = scannableService;
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Scannable> getScannableByBarcode(@PathVariable final String barcode) {
        log.info("Incoming request for scannable item with barcode {}", barcode);

        return scannableService.getByBarcode(barcode)
            .<ResponseEntity<Scannable>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/textid/{textIdentifier}")
    public ResponseEntity<Scannable> getScannableByTextIdentifier(@PathVariable final String textIdentifier) {
        log.info("Incoming request for scannable with textIdentifier: {}", textIdentifier);

        return scannableService.getByTextIdentifier(textIdentifier)
            .<ResponseEntity<Scannable>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getScannablesCount() {
        log.info("Incoming request for scannables count");

        return ResponseEntity.ok(scannableService.getScannableCount());
    }
}
