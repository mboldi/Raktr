package hu.bsstudio.raktr.ticket.controller;

import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import hu.bsstudio.raktr.dto.comment.CommentCreateDto;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketCreateDto;
import hu.bsstudio.raktr.dto.ticket.TicketDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketUpdateDto;
import hu.bsstudio.raktr.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tickets")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ticket")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<TicketDetailsDto> listTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) ProblemSeverity severity
    ) {
        return ticketService.listTickets(status, severity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDetailsDto createTicket(@RequestBody @Valid TicketCreateDto createDto) {
        return ticketService.createTicket(createDto);
    }

    @GetMapping("/{ticketId}")
    public TicketDetailsDto getTicketById(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @PutMapping("/{ticketId}")
    public TicketDetailsDto updateTicket(
            @PathVariable Long ticketId,
            @RequestBody @Valid TicketUpdateDto updateDto
    ) {
        return ticketService.updateTicket(ticketId, updateDto);
    }

    @PostMapping("/{ticketId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDetailsDto addCommentToTicket(
            @PathVariable Long ticketId,
            @RequestBody @Valid CommentCreateDto createDto
    ) {
        return ticketService.addCommentToTicket(ticketId, createDto);
    }

}
