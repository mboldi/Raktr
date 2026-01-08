package hu.bsstudio.raktr.device.mapper;

import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dto.device.DeviceCreateDto;
import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import hu.bsstudio.raktr.dto.device.DeviceUpdateDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = DateTimeMapper.class)
public interface DeviceMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "location", source = "location.name")
    DeviceDetailsDto entityToDetailsDto(Device device);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Device createDtoToEntity(DeviceCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDtoToEntity(@MappingTarget Device device, DeviceUpdateDto updateDto);

}
