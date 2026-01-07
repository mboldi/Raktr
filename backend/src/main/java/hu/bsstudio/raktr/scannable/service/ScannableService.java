package hu.bsstudio.raktr.scannable.service;

import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.scannable.mapper.ScannableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScannableService {

    private final ScannableRepository scannableRepository;

    private final ScannableMapper scannableMapper;

    public Long getCount() {
        return scannableRepository.count();
    }

    public ScannableDetailsDto getScannableByBarcode(String barcode) {
        var scannable = scannableRepository.findByBarcode(barcode).orElseThrow(ObjectNotFoundException::new);
        return scannableMapper.entityToDetailsDto(scannable);
    }

    public void existsByBarcode(String barcode) {
        if (!scannableRepository.existsByBarcode(barcode)) {
            throw new ObjectNotFoundException();
        }
    }

    public ScannableDetailsDto getScannableByAssetTag(String assetTag) {
        var scannable = scannableRepository.findByAssetTag(assetTag).orElseThrow(ObjectNotFoundException::new);
        return scannableMapper.entityToDetailsDto(scannable);
    }

    public void existsByAssetTag(String assetTag) {
        if (!scannableRepository.existsByAssetTag(assetTag)) {
            throw new ObjectNotFoundException();
        }
    }

}
