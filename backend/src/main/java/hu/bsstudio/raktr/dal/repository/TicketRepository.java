package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.entity.Ticket;
import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            SELECT t FROM Ticket t
            WHERE (:status IS NULL OR t.status = :status)
                AND (:severity IS NULL OR t.severity = :severity)
            """)
    List<Ticket> findByFilters(TicketStatus status, ProblemSeverity severity);

    List<Ticket> findByScannable(Scannable scannable);

}
