package hu.bsstudio.raktr.dal.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CONTAINER")
@Getter
@Setter
public class Container extends Scannable {

    @ManyToMany
    @JoinTable(
            name = "container_devices",
            joinColumns = @JoinColumn(name = "container_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    private List<Device> devices = new ArrayList<>();

    public Integer getTotalWeight() {
        var devicesWeight = devices.stream()
                .mapToInt(Device::getWeight)
                .sum();
        return getWeight() + devicesWeight;
    }

}
