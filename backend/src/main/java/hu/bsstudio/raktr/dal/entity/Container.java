package hu.bsstudio.raktr.dal.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CONTAINER")
@Getter
@Setter
public class Container extends Scannable {

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContainerItem> items = new ArrayList<>();

    public Integer getTotalWeight() {
        var devicesWeight = items.stream()
                .mapToInt(item -> item.getDevice().getWeight() * item.getQuantity())
                .sum();
        return getWeight() + devicesWeight;
    }

}
