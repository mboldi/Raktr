package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("checkstyle:FinalClass")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "devices")
@JsonSerialize()
@JsonDeserialize(builder = Device.DeviceBuilder.class)
@NoArgsConstructor
@Data
@PrimaryKeyJoinColumn(name = "id")
public class Device extends Scannable {

    @NotNull
    private String maker;

    @NotNull
    private String type;

    @NotNull
    private String serial;

    @NotNull
    private Integer value;

    @Min(0)
    private Integer weight;

    @NotNull
    private DeviceStatus status;

    @NotNull
    @Min(1)
    private Integer quantity;

    @ManyToMany
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private List<CompositeItem> parentComposites;

    @Builder
    public Device(Long id, String name, String barcode, String textIdentifier,
                  @NotNull Boolean isPublicRentable, List<RentItem> rentItems,
                  @NotNull String maker, @NotNull String type, @NotNull String serial,
                  @NotNull Integer value, @Min(0) Integer weight, @NotNull Location location,
                  @NotNull DeviceStatus status, @NotNull Category category,
                  @NotNull @Min(1) Integer quantity,
                  List<CompositeItem> parentComposites) {
        super(id, name, textIdentifier, barcode, isPublicRentable, location, category, rentItems);
        this.maker = maker;
        this.type = type;
        this.serial = serial;
        this.value = value;
        this.weight = weight;
        this.status = status;
        this.quantity = quantity;
        this.parentComposites = parentComposites;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class DeviceBuilder {}

    @Override
    public String toString() {
        return "Device{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", barcode='" + barcode + '\''
            + ", textIdentifier='" + textIdentifier + "'"
            + ", public_rentable='" + isPublicRentable + "'"
            + ", maker='" + maker + '\''
            + ", type='" + type + '\''
            + ", serial='" + serial + '\''
            + ", value=" + value
            + ", weight=" + weight
            + ", location=" + location
            + ", status=" + status
            + ", category=" + category
            + ", quantity=" + quantity
            + '}';
    }

}
