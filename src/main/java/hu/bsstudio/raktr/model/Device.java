package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("checkstyle:FinalClass")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "devices")
@JsonSerialize()
@JsonDeserialize(builder = Device.Builder.class)
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
    @ManyToOne
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Location location;

    @NotNull
    private DeviceStatus status;

    @NotNull
    @JoinColumn
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category;

    @NotNull
    @Min(1)
    private Integer quantity;

    @ManyToMany
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private List<CompositeItem> parentComposites;

    private Device(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.maker = builder.maker;
        this.type = builder.type;
        this.serial = builder.serial;
        this.value = builder.value;
        this.barcode = builder.barcode;
        this.weight = builder.weight;
        this.location = builder.location;
        this.status = builder.status;
        this.category = builder.category;
        this.quantity = builder.quantity;
        this.textIdentifier = builder.textIdentifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Device{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", barcode='" + barcode + '\''
            + ", textIdentifier='" + textIdentifier + "'"
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

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String name;
        private String maker;
        private String type;
        private String serial;
        private Integer value;
        private String barcode;
        private String textIdentifier;
        private Integer weight;
        private Location location;
        private DeviceStatus status;
        private Category category;
        private Integer quantity;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withMaker(final String maker) {
            this.maker = maker;
            return this;
        }

        public Builder withType(final String type) {
            this.type = type;
            return this;
        }

        public Builder withSerial(final String serial) {
            this.serial = serial;
            return this;
        }

        public Builder withValue(final Integer value) {
            this.value = value;
            return this;
        }

        public Builder withBarcode(final String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder withTextIdentifier(final String textIdentifier) {
            this.textIdentifier = textIdentifier;
            return this;
        }

        public Builder withWeight(final Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder withLocation(final Location location) {
            this.location = location;
            return this;
        }

        public Builder withStatus(final DeviceStatus status) {
            this.status = status;
            return this;
        }

        public Builder withCategory(final Category category) {
            this.category = category;
            return this;
        }

        public Builder withQuantity(final Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }
}
