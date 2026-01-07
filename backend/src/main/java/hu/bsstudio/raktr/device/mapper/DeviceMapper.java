package hu.bsstudio.raktr.device.mapper;

import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateTimeMapper.class)
public interface DeviceMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "location", source = "location.name")
    DeviceDetailsDto entityToDetailsDto(Device device);

}