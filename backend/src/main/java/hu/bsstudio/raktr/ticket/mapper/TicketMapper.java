package hu.bsstudio.raktr.ticket.mapper;

import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.entity.Ticket;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
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

    default TicketDetailsDto.Scannable scannableToDto(Scannable scannable) {
        if (scannable == null) {
            return null;
        }

        var dto = new TicketDetailsDto.Scannable();
        dto.setId(scannable.getId());
        dto.setAssetTag(scannable.getAssetTag());
        dto.setName(scannable.getName());

        if (scannable.getOwner() != null) {
            var owner = new ScannableDetailsDto.Owner();
            owner.setId(scannable.getOwner().getId());
            owner.setName(scannable.getOwner().getName());
            dto.setOwner(owner);
        }

        if (scannable instanceof Device device) {
            dto.setManufacturer(device.getManufacturer());
            dto.setAcquisitionSource(device.getAcquisitionSource());
            dto.setAcquisitionDate(device.getAcquisitionDate());
            dto.setWarrantyEndDate(device.getWarrantyEndDate());
        }

        return dto;
    }

}
