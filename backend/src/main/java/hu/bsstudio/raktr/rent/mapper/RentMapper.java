package hu.bsstudio.raktr.rent.mapper;

import hu.bsstudio.raktr.dal.entity.Rent;
import hu.bsstudio.raktr.dto.rent.RentCreateDto;
import hu.bsstudio.raktr.dto.rent.RentDetailsDto;
import hu.bsstudio.raktr.dto.rent.RentUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {DateTimeMapper.class, RentItemMapper.class})
public interface RentMapper {

    RentDetailsDto entityToDetailsDto(Rent rent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issuer", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "closed", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "rentItems", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Rent createDtoToEntity(RentCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "issuer", ignore = true)
    @Mapping(target = "closed", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "rentItems", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDtoToEntity(@MappingTarget Rent rent, RentUpdateDto updateDto);

}
