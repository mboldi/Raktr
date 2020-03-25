package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Scannable;
import hu.bsstudio.raktr.service.ScannableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/scannable")
public final class ScannableController {

    private final ScannableService scannableService;

    public ScannableController(final ScannableService scannableService) {
        this.scannableService = scannableService;
    }

    @GetMapping("/{barcode}")
    public Scannable getScannableById(@PathVariable final String barcode) {
        log.info("Incoming request for scannable item with barcode {}", barcode);
        return scannableService.getByBarcode(barcode);
    }
}
