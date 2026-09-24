package hu.bsstudio.raktr.dto.appconfig;

import hu.bsstudio.raktr.dal.value.ConfigDataType;
import hu.bsstudio.raktr.dal.value.ConfigKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDetailsDto {

    private ConfigKey key;

    private String value;

    private ConfigDataType dataType;

}
