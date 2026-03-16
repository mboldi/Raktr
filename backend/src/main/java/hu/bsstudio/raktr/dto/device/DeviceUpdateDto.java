package hu.bsstudio.raktr.dto.device;

import hu.bsstudio.raktr.dal.value.DeviceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdateDto {

    @NotBlank
    private String assetTag;

    @NotBlank
    private String barcode;

    @NotBlank
    private String name;

    @Min(1)
    @NotNull
    private Integer weight;

    private boolean publicRentable;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String locationName;

    @Min(1)
    @NotNull
    private Long ownerId;

    private String manufacturer;

    private String model;

    private String serialNumber;

    private Integer estimatedValue;

    @NotNull
    private DeviceStatus status;

    private Integer quantity;

    private String acquisitionSource;

    private LocalDate acquisitionDate;

    private LocalDate warrantyEndDate;

    private String notes;

}
