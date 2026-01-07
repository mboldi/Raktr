package hu.bsstudio.raktr.scannable.mapper;

import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateTimeMapper.class)
public interface ScannableMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "location", source = "location.name")
    ScannableDetailsDto entityToDetailsDto(Scannable scannable);

}
