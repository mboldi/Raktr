package hu.bsstudio.raktr.config.mapper;

import hu.bsstudio.raktr.dal.entity.Config;
import hu.bsstudio.raktr.dto.appconfig.ConfigDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ConfigMapper {

    @Mapping(target = "dataType", source = "key.dataType")
    ConfigDetailsDto entityToDetailsDto(Config config);

}
