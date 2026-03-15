package hu.bsstudio.raktr.dto.appconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDetailsDto {

    private String key;

    private String value;

    private String dataType;

}
