package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT ticket FROM Ticket ticket WHERE ticket.scannableOfProblem.id = :scannableId")
    List<Ticket> findByScannableId(Long scannableId);
}
