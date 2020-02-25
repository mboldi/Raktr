package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "devices")
@JsonSerialize
@JsonDeserialize(builder = Device.Builder.class)
@NoArgsConstructor
@Data()
public class Device extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String maker;

    private String type;

    private String serial;

    private int value;

    private String barcode;

    private int weight;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Location location;

    private int status;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Category category;

    private Device(Builder builder) {
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
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private String name;
        private String maker;
        private String type;
        private String serial;
        private int value;
        private String barcode;
        private int weight;
        private Location location;
        private int status;
        private Category category;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withMaker(String maker) {
            this.maker = maker;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withSerial(String serial) {
            this.serial = serial;
            return this;
        }

        public Builder withValue(int value) {
            this.value = value;
            return this;
        }

        public Builder withBarcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        private Builder withWeight(int weight) {
            this.weight = weight;
            return this;
        }

        private Builder withLocation(Location location) {
            this.location = location;
            return this;
        }

        private Builder withStatus(int status) {
            this.status = status;
            return this;
        }

        private Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }

}
