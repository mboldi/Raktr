package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "devices")
@JsonSerialize()
@JsonDeserialize(builder = Device.Builder.class)
@NoArgsConstructor
@Data
public final class Device extends DomainAuditModel {

    public static final int MAX_STATUS_RATING = 5;
    public static final int MIN_STATUS_RATING = 0;
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
    @Column(unique = true)
    private String serial;

    @NotNull
    private Integer value;

    @NotNull
    @Column(unique = true)
    private String barcode;

    @Min(0)
    private Integer weight;

    @NotNull
    @ManyToOne
    @JoinColumn
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Location location;

    @Min(MIN_STATUS_RATING)
    @Max(MAX_STATUS_RATING)
    private int status;

    @NotNull
    @JoinColumn
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category;

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
    }

    public static Builder builder() {
        return new Builder();
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
        private Integer weight;
        private Location location;
        private Integer status;
        private Category category;

        public Device.Builder withId(final Long id) {
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

        public Builder withWeight(final Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder withLocation(final Location location) {
            this.location = location;
            return this;
        }

        public Builder withStatus(final Integer status) {
            this.status = status;
            return this;
        }

        public Builder withCategory(final Category category) {
            this.category = category;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }

}
