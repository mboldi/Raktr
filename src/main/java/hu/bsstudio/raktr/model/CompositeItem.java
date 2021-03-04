package hu.bsstudio.raktr.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@JsonSerialize
@JsonDeserialize(builder = CompositeItem.Builder.class)
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

    public CompositeItem(final Builder builder) {
        this.id = builder.id;
        this.barcode = builder.barcode;
        this.textIdentifier = builder.textIdentifier;
        this.name = builder.name;
        this.devices = builder.devices;
        this.location = builder.location;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    public String toString() {
        return "CompositeItem{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", barcode='" + barcode + '\''
            + ", textIdentifier='" + textIdentifier + '\''
            + ", devices=" + devices
            + ", location=" + location
            + '}';
    }

    @SuppressWarnings("hiddenField")
    public static final class Builder {
        private Long id;
        private String name;
        private String barcode;
        private String textIdentifier;
        private List<Device> devices;
        private Location location;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
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

        public Builder withDevices(final List<Device> devices) {
            this.devices = devices;
            return this;
        }

        public Builder withLocation(final Location location) {
            this.location = location;
            return this;
        }

        public CompositeItem build() {
            return new CompositeItem(this);
        }
    }
}
