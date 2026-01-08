package hu.bsstudio.raktr.ticket.service;

import hu.bsstudio.raktr.comment.mapper.CommentMapper;
import hu.bsstudio.raktr.dal.entity.Ticket;
import hu.bsstudio.raktr.dal.repository.CommentRepository;
import hu.bsstudio.raktr.dal.repository.TicketRepository;
import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import hu.bsstudio.raktr.dto.comment.CommentCreateDto;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketCreateDto;
import hu.bsstudio.raktr.dto.ticket.TicketDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketUpdateDto;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import hu.bsstudio.raktr.ticket.mapper.TicketMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    private final CommentRepository commentRepository;

    private final ScannableLookupService lookupService;

    private final TicketMapper ticketMapper;

    private final CommentMapper commentMapper;

    @Transactional
    public List<TicketDetailsDto> listTickets(TicketStatus status, ProblemSeverity severity) {
        var tickets = ticketRepository.findByFilters(status, severity);
        return tickets.stream().map(ticketMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public TicketDetailsDto createTicket(TicketCreateDto createDto) {
        var ticket = ticketMapper.createDtoToEntity(createDto);

        var scannable = lookupService.getScannable(createDto.getScannableId());
        ticket.setScannable(scannable);

        ticket = ticketRepository.saveAndFlush(ticket);

        log.info("Created Ticket with ID [{}]", ticket.getId());

        return ticketMapper.entityToDetailsDto(ticket);
    }

    @Transactional
    public TicketDetailsDto getTicketById(Long ticketId) {
        var ticket = getTicket(ticketId);
        return ticketMapper.entityToDetailsDto(ticket);
    }

    @Transactional
    public TicketDetailsDto updateTicket(Long ticketId, TicketUpdateDto updateDto) {
        var ticket = getTicket(ticketId);

        ticketMapper.updateDtoToEntity(ticket, updateDto);

        ticketRepository.saveAndFlush(ticket);

        log.info("Updated Ticked with ID [{}]", ticketId);

        return ticketMapper.entityToDetailsDto(ticket);
    }

    @Transactional
    public CommentDetailsDto addCommentToTicket(Long ticketId, CommentCreateDto createDto) {
        var ticket = getTicket(ticketId);

        var comment = commentMapper.createDtoToEntity(createDto);
        comment = commentRepository.saveAndFlush(comment);

        ticket.getComments().add(comment);

        log.info("Added Comment [{}] to Ticket [{}]", comment.getId(), ticketId);

        return commentMapper.entityToDetailsDto(comment);
    }

    @Transactional
    public List<TicketDetailsDto> getTicketsByScannableId(Long scannableId) {
        var scannable = lookupService.getScannable(scannableId);
        var tickets = ticketRepository.findByScannable(scannable);
        return tickets.stream().map(ticketMapper::entityToDetailsDto).toList();
    }

    private Ticket getTicket(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(ObjectNotFoundException::new);
    }

}
