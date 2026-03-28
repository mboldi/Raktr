package hu.bsstudio.raktr.dto.device;

import hu.bsstudio.raktr.dal.value.DeviceStatus;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceDetailsDto extends ScannableDetailsDto {

    private String manufacturer;

    private String model;

    private String serialNumber;

    private Integer estimatedValue;

    private DeviceStatus status;

    private Integer quantity;

    private String acquisitionSource;

    private LocalDate acquisitionDate;

    private LocalDate warrantyEndDate;

    private String notes;

}
