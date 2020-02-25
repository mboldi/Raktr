package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "devices")
@JsonSerialize()
@JsonDeserialize(builder = Device.Builder.class)
@NoArgsConstructor
@Data
public class Device extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String maker;

    @NotNull
    private String type;

    @NotNull
    private String serial;

    @NotNull
    private Integer value;

    @NotNull
    private String barcode;

    @Min(0)
    private Integer weight;
/*
    @NotNull
    @ManyToOne
    @JoinColumn
    private Location location;*/

    @Min(0)
    @Max(5)
    private int status;
/*
    @NotNull
    @ManyToOne
    @JoinColumn
    private Category category;
*/
    private Device(final Builder builder) {
        this.name = builder.name;
        this.maker = builder.maker;
        this.type = builder.type;
        this.serial = builder.serial;
        this.value = builder.value;
        this.barcode = builder.barcode;
        this.weight = builder.weight;
        //this.location = builder.location;
        this.status = builder.status;
        //this.category = builder.category;
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
        private Integer value;
        private String barcode;
        private Integer weight;
        //private Location location;
        private Integer status;
        //private Category category;

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

        public Builder withWeight(final Integer weight) {
            this.weight = weight;
            return this;
        }
/*
        public Builder withLocation(final Location location) {
            this.location = location;
            return this;
        }*/

        public Builder withStatus(final Integer status) {
            this.status = status;
            return this;
        }
/*
        public Builder withCategory(final Category category) {
            this.category = category;
            return this;
        }*/

        public Device build() {
            return new Device(this);
        }
    }

}
