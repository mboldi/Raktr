package hu.bsstudio.raktr.rent.mapper;

import hu.bsstudio.raktr.dal.entity.RentItem;
import hu.bsstudio.raktr.dto.rentitem.RentItemCreateDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemDetailsDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import hu.bsstudio.raktr.scannable.mapper.ScannableMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {DateTimeMapper.class, ScannableMapper.class})
public interface RentItemMapper {

    RentItemDetailsDto entityToDetailsDto(RentItem rentItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rent", ignore = true)
    @Mapping(target = "scannable", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RentItem createDtoToEntity(RentItemCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rent", ignore = true)
    @Mapping(target = "scannable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDtoToEntity(@MappingTarget RentItem rentItem, RentItemUpdateDto updateDto);

}
