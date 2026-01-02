package hu.bsstudio.raktr.location.mapper;

import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dto.location.LocationCreateDto;
import hu.bsstudio.raktr.dto.location.LocationDetailsDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateTimeMapper.class)
public interface LocationMapper {

    LocationDetailsDto entityToDetailsDto(Location location);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Location createDtoToEntity(LocationCreateDto createDto);

}
