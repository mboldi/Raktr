package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT ticket FROM Ticket ticket WHERE ticket.scannableOfProblem.id = :scannableId")
    List<Ticket> findByScannableId(Long scannableId);

}
