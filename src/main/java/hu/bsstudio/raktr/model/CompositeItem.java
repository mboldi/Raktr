package hu.bsstudio.raktr.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@JsonSerialize
@JsonDeserialize(builder = CompositeItem.CompositeItemBuilder.class)
@NoArgsConstructor
@Data
@PrimaryKeyJoinColumn(name = "id")
public class CompositeItem extends Scannable {

    @ManyToMany(targetEntity = Device.class, cascade = REFRESH, fetch = EAGER)
    @Setter(AccessLevel.NONE)
    private List<Device> devices;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Location location;

    @Builder
    public CompositeItem(Long id, @NotBlank String name, @NotBlank String textIdentifier, @NotNull String barcode,
                         @NotNull Boolean isPublicRentable, List<RentItem> rentItems,
                         List<Device> devices, @NotNull Location location) {
        super(id, name, textIdentifier, barcode, isPublicRentable, rentItems);
        this.devices = devices;
        this.location = location;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class CompositeItemBuilder{}

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    public String toString() {
        return "CompositeItem{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", barcode='" + barcode + '\''
            + ", textIdentifier='" + textIdentifier + '\''
            + ", public_rentable='" + isPublicRentable + "'"
            + ", devices=" + devices
            + ", location=" + location
            + '}';
    }

}
