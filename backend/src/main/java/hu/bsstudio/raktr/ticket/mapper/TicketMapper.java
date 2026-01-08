package hu.bsstudio.raktr.ticket.mapper;

import hu.bsstudio.raktr.dal.entity.Ticket;
import hu.bsstudio.raktr.dto.ticket.TicketCreateDto;
import hu.bsstudio.raktr.dto.ticket.TicketDetailsDto;
import hu.bsstudio.raktr.dto.ticket.TicketUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = DateTimeMapper.class)
public interface TicketMapper {

    TicketDetailsDto entityToDetailsDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scannable", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Ticket createDtoToEntity(TicketCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scannable", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDtoToEntity(@MappingTarget Ticket ticket, TicketUpdateDto updateDto);

}
