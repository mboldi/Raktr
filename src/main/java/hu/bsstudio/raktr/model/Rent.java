package hu.bsstudio.raktr.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rents")
@JsonSerialize()
@JsonDeserialize(builder = Rent.Builder.class)
@NoArgsConstructor
@Data
public class Rent extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String destination;

    @NotBlank
    private String issuer;

    @NotBlank
    private String outDate;

    @NotBlank
    private String expBackDate;

    @NotNull
    private String actBackDate;

    @OneToMany(targetEntity = DeviceRentItem.class, cascade = REFRESH, fetch = EAGER)
    @Setter(AccessLevel.NONE)
    private List<DeviceRentItem> devices;

    public Rent(final Builder builder) {
        this.id = builder.id;
        this.destination = builder.destination;
        this.outDate = builder.outDate;
        this.expBackDate = builder.expBackDate;
        this.actBackDate = builder.actBackDate;
        this.devices = builder.devices;
        this.issuer = builder.issuer;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String destination;
        private String issuer;
        private String outDate;
        private String expBackDate;
        private String actBackDate;
        private List<DeviceRentItem> devices;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withDestination(final String destination) {
            this.destination = destination;
            return this;
        }

        public Builder withIssuer(final String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder withOutDate(final String outDate) {
            this.outDate = outDate;
            return this;
        }

        public Builder withExpBackDate(final String expBackDate) {
            this.expBackDate = expBackDate;
            return this;
        }

        public Builder withActBackDate(final String actBackDate) {
            this.actBackDate = actBackDate;
            return this;
        }

        public Builder withDevices(final List<DeviceRentItem> devices) {
            this.devices = devices;
            return this;
        }

        public Rent build() {
            return new Rent(this);
        }
    }
}
