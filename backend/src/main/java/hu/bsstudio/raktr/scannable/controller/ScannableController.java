package hu.bsstudio.raktr.scannable.controller;

import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.scannable.service.ScannableService;
import hu.bsstudio.raktr.security.RoleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scannable")
public class ScannableController {

    private final ScannableService scannableService;

    @Secured({RoleConstants.ADMIN, RoleConstants.MEMBER})
    @DeleteMapping("/{scannableId}")
    public void deleteScannable(@PathVariable Long scannableId) {
        scannableService.deleteScannable(scannableId);
    }

    @Secured(RoleConstants.ADMIN)
    @PostMapping("/{scannableId}/restore")
    public void restoreScannable(@PathVariable Long scannableId) {
        scannableService.restoreScannable(scannableId);
    }

    // TODO: Check this later (would probably make sense to use paginated list endpoint)
    @GetMapping("/count")
    public Long getCount() {
        return scannableService.getCount();
    }

    @GetMapping(value = "/barcode/{barcode}")
    public ScannableDetailsDto getScannableByBarcode(@PathVariable String barcode) {
        return scannableService.getScannableByBarcode(barcode);
    }

    @RequestMapping(value = "/barcode/{barcode}", method = RequestMethod.HEAD)
    public void checkBarcodeExists(@PathVariable String barcode) {
        scannableService.existsByBarcode(barcode);
    }

    @GetMapping(value = "/asset-tag/{assetTag}")
    public ScannableDetailsDto getScannableByAssetTag(@PathVariable String assetTag) {
        return scannableService.getScannableByAssetTag(assetTag);
    }

    @RequestMapping(value = "/asset-tag/{assetTag}", method = RequestMethod.HEAD)
    public void checkAssetTagExists(@PathVariable String assetTag) {
        scannableService.existsByAssetTag(assetTag);
    }

}
