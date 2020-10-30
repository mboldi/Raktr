package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.GeneralData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GeneralDataDao extends JpaRepository<GeneralData, String> {
}
