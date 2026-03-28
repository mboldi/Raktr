package hu.bsstudio.raktr.dto.container;

import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerItemDetailsDto {

    private DeviceDetailsDto device;

    private Integer quantity;

}
