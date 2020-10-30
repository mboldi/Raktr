package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.GeneralDataDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.GeneralData;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class GeneralDataService {
    private final GeneralDataDao generalDataDao;

    public GeneralDataService(final GeneralDataDao generalDataDao) {
        this.generalDataDao = generalDataDao;
    }

    public GeneralData create(final GeneralData newData) {
        GeneralData saved = generalDataDao.save(newData);

        log.info("General data saved: {}", saved);
        return saved;
    }

    public List<GeneralData> getAll() {
        List<GeneralData> fetched = generalDataDao.findAll();

        log.info("All general data fetched: {}", fetched);
        return fetched;
    }

    public GeneralData getByKey(final String key) {
        Optional<GeneralData> found = generalDataDao.findById(key);

        if (found.isEmpty()) {
            log.error("General data by key {} not found", key);
            throw new ObjectNotFoundException();
        }

        log.info("General data by id {} found: {}", key, found.get());

        return found.get();
    }

    public GeneralData update(final GeneralData dataToUpdate) {
        Optional<GeneralData> foundData = generalDataDao.findById(dataToUpdate.getKey());

        if (foundData.isEmpty()) {
            GeneralData saved = generalDataDao.save(dataToUpdate);
            log.info("General data to update not found, so created: {}", saved);
            return saved;
        }

        GeneralData generalDataToUpdate = foundData.get();
        generalDataToUpdate.setData(dataToUpdate.getData());

        GeneralData saved = generalDataDao.save(generalDataToUpdate);
        log.info("General data updated: {}", saved);
        return saved;
    }

    public GeneralData delete(final GeneralData dataToDelete) {
        Optional<GeneralData> found = generalDataDao.findById(dataToDelete.getKey());

        if (found.isEmpty()) {
            log.error("General data not in database, couldn't delete: {}", dataToDelete);
            throw new ObjectNotFoundException();
        }

        generalDataDao.delete(dataToDelete);
        return dataToDelete;
    }
}
