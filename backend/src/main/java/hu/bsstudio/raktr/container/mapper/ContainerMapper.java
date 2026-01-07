package hu.bsstudio.raktr.container.mapper;

import hu.bsstudio.raktr.dal.entity.Container;
import hu.bsstudio.raktr.device.mapper.DeviceMapper;
import hu.bsstudio.raktr.dto.container.ContainerCreateDto;
import hu.bsstudio.raktr.dto.container.ContainerDetailsDto;
import hu.bsstudio.raktr.dto.container.ContainerUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {DateTimeMapper.class, DeviceMapper.class})
public interface ContainerMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "location", source = "location.name")
    ContainerDetailsDto entityToDetailsDto(Container container);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "devices", ignore = true)
    Container createDtoToEntity(ContainerCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "devices", ignore = true)
    void updateDtoToEntity(@MappingTarget Container container, ContainerUpdateDto updateDto);

}
