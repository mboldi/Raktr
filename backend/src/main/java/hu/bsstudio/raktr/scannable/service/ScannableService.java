package hu.bsstudio.raktr.scannable.service;

import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.scannable.mapper.ScannableMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScannableService {

    private final ScannableRepository scannableRepository;

    private final ScannableMapper scannableMapper;

    @Transactional
    public void deleteScannable(Long scannableId) {
        var scannable = getScannable(scannableId);
        scannable.setDeleted(true);
        scannableRepository.saveAndFlush(scannable);
        log.info("Deleted Scannable with ID [{}]", scannableId);
    }

    @Transactional
    public void restoreScannable(Long scannableId) {
        var scannable = getScannable(scannableId);
        scannable.setDeleted(false);
        scannableRepository.saveAndFlush(scannable);
        log.info("Restored Scannable with ID [{}]", scannableId);
    }

    public Long getCount() {
        return scannableRepository.count();
    }

    public ScannableDetailsDto getScannableByBarcode(String barcode) {
        var scannable = scannableRepository.findByBarcode(barcode)
                .orElseThrow(() -> new EntityNotFoundException(Scannable.class, barcode));
        return scannableMapper.entityToDetailsDto(scannable);
    }

    public void existsByBarcode(String barcode) {
        if (!scannableRepository.existsByBarcode(barcode)) {
            throw new EntityNotFoundException(Scannable.class, barcode);
        }
    }

    public ScannableDetailsDto getScannableByAssetTag(String assetTag) {
        var scannable = scannableRepository.findByAssetTag(assetTag)
                .orElseThrow(() -> new EntityNotFoundException(Scannable.class, assetTag));
        return scannableMapper.entityToDetailsDto(scannable);
    }

    public void existsByAssetTag(String assetTag) {
        if (!scannableRepository.existsByAssetTag(assetTag)) {
            throw new EntityNotFoundException(Scannable.class, assetTag);
        }
    }

    private Scannable getScannable(Long scannableId) {
        return scannableRepository.findById(scannableId)
                .orElseThrow(() -> new EntityNotFoundException(Scannable.class, scannableId));
    }

}
