package hu.bsstudio.raktr.scannable.controller;

import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.scannable.service.ScannableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scannables", description = "Lookup devices and containers by barcode or asset tag")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scannables")
public class ScannableController {

    private final ScannableService scannableService;

    // TODO: Check this later (would probably make sense to use paginated list endpoint)
    @GetMapping("/count")
    public Long getCount() {
        return scannableService.getCount();
    }

    @GetMapping(value = "/barcode/{barcode}")
    public ScannableDetailsDto getScannableByBarcode(@PathVariable String barcode) {
        return scannableService.getScannableByBarcode(barcode);
    }

    @Operation(summary = "Check if barcode exists", description = "Returns 200 if exists, 404 if not")
    @RequestMapping(value = "/barcode/{barcode}", method = RequestMethod.HEAD)
    public void checkBarcodeExists(@PathVariable String barcode) {
        scannableService.existsByBarcode(barcode);
    }

    @GetMapping(value = "/asset-tag/{assetTag}")
    public ScannableDetailsDto getScannableByAssetTag(@PathVariable String assetTag) {
        return scannableService.getScannableByAssetTag(assetTag);
    }

    @Operation(summary = "Check if asset tag exists", description = "Returns 200 if exists, 404 if not")
    @RequestMapping(value = "/asset-tag/{assetTag}", method = RequestMethod.HEAD)
    public void checkAssetTagExists(@PathVariable String assetTag) {
        scannableService.existsByAssetTag(assetTag);
    }

}
