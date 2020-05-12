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
    private String renter;

    @NotBlank
    private String issuer;

    @NotBlank
    private String outDate;

    @NotBlank
    private String expBackDate;

    @NotNull
    private String actBackDate;

    @OneToMany(targetEntity = RentItem.class, cascade = REFRESH, fetch = EAGER, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<RentItem> rentItems;

    public Rent(final Builder builder) {
        this.id = builder.id;
        this.destination = builder.destination;
        this.issuer = builder.issuer;
        this.renter = builder.renter;
        this.outDate = builder.outDate;
        this.expBackDate = builder.expBackDate;
        this.actBackDate = builder.actBackDate;
        this.rentItems = builder.rentItems;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    public RentItem getRentItemOfScannable(final Scannable scannableToFind) {
        for (RentItem rentItem : rentItems) {
            if (rentItem.getScannable().getId().equals(scannableToFind.getId())) {
                return rentItem;
            }
        }

        return null;
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String destination;
        private String issuer;
        private String renter;
        private String outDate;
        private String expBackDate;
        private String actBackDate;
        private List<RentItem> rentItems;

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

        public Builder withRenter(final String renter) {
            this.renter = renter;
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

        public Builder withRentItems(final List<RentItem> rentItems) {
            this.rentItems = rentItems;
            return this;
        }

        public Rent build() {
            return new Rent(this);
        }
    }
}
