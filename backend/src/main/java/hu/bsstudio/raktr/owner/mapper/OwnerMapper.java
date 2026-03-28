package hu.bsstudio.raktr.owner.mapper;

import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.dto.owner.OwnerCreateDto;
import hu.bsstudio.raktr.dto.owner.OwnerDetailsDto;
import hu.bsstudio.raktr.dto.owner.OwnerUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = DateTimeMapper.class)
public interface OwnerMapper {

    OwnerDetailsDto entityToDetailsDto(Owner owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Owner createDtoToEntity(OwnerCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDtoToEntity(@MappingTarget Owner owner, OwnerUpdateDto updateDto);

}
