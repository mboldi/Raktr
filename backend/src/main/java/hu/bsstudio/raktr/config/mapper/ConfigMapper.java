package hu.bsstudio.raktr.config.mapper;

import hu.bsstudio.raktr.dal.entity.Config;
import hu.bsstudio.raktr.dto.appconfig.ConfigDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface ConfigMapper {

    ConfigDetailsDto entityToDetailsDto(Config config);

}
