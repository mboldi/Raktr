package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.HistoryEvent;
import hu.bsstudio.raktr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface HistoryEventRepository extends JpaRepository<HistoryEvent, Long> {

    List<HistoryEvent> findByUser(final User user );

    List<HistoryEvent> findByDate( final Date date );
}
