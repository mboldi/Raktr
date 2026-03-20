package hu.bsstudio.raktr.dal.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "container_devices")
public class ContainerItem {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private ContainerItemId id = new ContainerItemId();

    @ManyToOne
    @MapsId("containerId")
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @ManyToOne
    @MapsId("deviceId")
    @JoinColumn(name = "device_id", nullable = false)
    @ToString.Include
    private Device device;

    @ToString.Include
    private Integer quantity;

}
