package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Ticket;
import hu.bsstudio.raktr.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<Ticket> getAllTickets() {
        log.info("Incoming request for all tickets");

        return ticketService.getAll();
    }

    @PostMapping
    public ResponseEntity<Ticket> addTicket(@Valid @RequestBody Ticket newTicket) {
        log.info("Incoming request to add ticket: {}", newTicket);

        Optional<Ticket> ticket = ticketService.addTicket(newTicket);

        return ticket
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PutMapping
    public ResponseEntity<Ticket> updateTicket(@Valid @RequestBody Ticket ticketToUpdate) {
        log.info("Incoming request to update ticket: {}", ticketToUpdate);

        Optional<Ticket> ticket = ticketService.updateTicket(ticketToUpdate);

        return ticket
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }

    @GetMapping("/ofscannable/{scannableId}")
    public List<Ticket> getTicketsOfScannable(@PathVariable final Long scannableId) {
        log.info("Incoming request for rentITems of scannable item with id {}", scannableId);

        return ticketService.getTicketsOfScannable(scannableId);
    }
}
