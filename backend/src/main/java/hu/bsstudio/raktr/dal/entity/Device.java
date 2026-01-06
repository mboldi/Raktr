package hu.bsstudio.raktr.dal.entity;

import hu.bsstudio.raktr.dal.value.DeviceStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("DEVICE")
@Getter
@Setter
public class Device extends Scannable {

    private String manufacturer;

    private String model;

    private String serialNumber;

    private Integer estimatedValue;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Integer quantity;

    private String acquisitionSource;

    private LocalDate acquisitionDate;

    private LocalDate warrantyEndDate;

    private String notes;

}
