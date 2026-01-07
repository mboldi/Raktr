package hu.bsstudio.raktr.dto.container;

import hu.bsstudio.raktr.dto.device.DeviceDetailsDto;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContainerDetailsDto extends ScannableDetailsDto {

    private Integer totalWeight;

    private List<DeviceDetailsDto> devices;

}
