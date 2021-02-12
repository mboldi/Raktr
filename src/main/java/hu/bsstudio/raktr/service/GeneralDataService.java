package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.GeneralDataRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.GeneralData;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public final class GeneralDataService {
    private final GeneralDataRepository generalDataRepository;

    public Optional<GeneralData> create(final GeneralData newData) {
        final var foundData = generalDataRepository.findById(newData.getKey());

        if(foundData.isPresent()){
            log.info("General Data item with same key exists in database: {}", foundData.get());
            return Optional.empty();
        }

        GeneralData saved = generalDataRepository.save(newData);

        log.info("General data saved: {}", saved);
        return Optional.of(saved);
    }

    public List<GeneralData> getAll() {
        List<GeneralData> fetched = generalDataRepository.findAll();

        log.info("All general data fetched: {}", fetched);
        return fetched;
    }

    public Optional<GeneralData> getByKey(final String key) {
        Optional<GeneralData> found = generalDataRepository.findById(key);

        if (found.isEmpty()) {
            log.error("General data by key {} not found", key);
            throw new ObjectNotFoundException();
        }

        log.info("General data by id {} found: {}", key, found.get());

        return found;
    }

    public GeneralData update(final GeneralData dataToUpdate) {
        Optional<GeneralData> foundData = generalDataRepository.findById(dataToUpdate.getKey());

        if (foundData.isEmpty()) {
            GeneralData saved = generalDataRepository.save(dataToUpdate);
            log.info("General data to update not found, so created: {}", saved);
            return saved;
        }

        GeneralData generalDataToUpdate = foundData.get();
        generalDataToUpdate.setData(dataToUpdate.getData());

        GeneralData saved = generalDataRepository.save(generalDataToUpdate);
        log.info("General data updated: {}", saved);
        return saved;
    }

    public Optional<GeneralData> delete(final GeneralData dataToDelete) {
        Optional<GeneralData> found = generalDataRepository.findById(dataToDelete.getKey());

        if (found.isEmpty()) {
            log.error("General data not in database, couldn't delete: {}", dataToDelete);
            return Optional.empty();
        }

        generalDataRepository.delete(dataToDelete);
        return found;
    }
}
