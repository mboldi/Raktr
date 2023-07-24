package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.Comment;
import hu.bsstudio.raktr.model.Ticket;
import hu.bsstudio.raktr.repository.CommentRepository;
import hu.bsstudio.raktr.repository.TicketRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    public List<Ticket> getAll() {
        List<Ticket> allTickets = ticketRepository.findAll();

        log.info("Tickets found: {}", allTickets);

        return allTickets;
    }

    public Optional<Ticket> get(final Long ticketId) {
        var ticket = ticketRepository.findById(ticketId);

        log.info("Ticket found: {}", ticket);

        return ticket;
    }

    public Optional<Ticket> addTicket(final Ticket newTicket) {
        var foundTicket = newTicket.getId() == null ? Optional.empty() : ticketRepository.findById(newTicket.getId());

        if (foundTicket.isPresent()) {
            return Optional.empty();
        }

        Ticket saved = ticketRepository.save(newTicket);

        log.info("Saved new ticket: {}", saved);

        return Optional.of(saved);
    }

    public Optional<Ticket> updateTicket(final Ticket ticketToUpdate) {
        var foundTicket = ticketRepository.findById(ticketToUpdate.getId());

        if (foundTicket.isEmpty()) {
            return Optional.empty();
        }

        foundTicket.get().setStatus(ticketToUpdate.getStatus());
        foundTicket.get().setSeverity(ticketToUpdate.getSeverity());
        foundTicket.get().setBody(ticketToUpdate.getBody());

        Ticket updated = ticketRepository.save(foundTicket.get());

        log.info("Updated ticket: {}", updated);

        return Optional.of(updated);
    }

    public List<Ticket> getTicketsOfScannable(final Long scannableId) {
        List<Ticket> ticketsOfScannable = ticketRepository.findByScannableId(scannableId);

        if (ticketsOfScannable.isEmpty()) {
            log.info("No tickets found for scannable with id {}", scannableId);
        } else {
            log.info("Tickets found for scannable: {}", ticketsOfScannable);
        }

        return ticketsOfScannable;
    }

    public Optional<Ticket> addCommentToTicket(final Long ticketId, final Comment comment) {
        var ticketToUpdate = ticketRepository.findById(ticketId);

        if (ticketToUpdate.isEmpty()) {
            log.warn("Ticket not found to add comment to with id: {}", ticketId);
            return Optional.empty();
        }

        var savedComment = commentRepository.save(comment);

        ticketToUpdate.get().addComment(savedComment);

        ticketRepository.save(ticketToUpdate.get());

        log.info("Saved ticket with comment: {}", ticketToUpdate.get());

        return ticketToUpdate;
    }
}
