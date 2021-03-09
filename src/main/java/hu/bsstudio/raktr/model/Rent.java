package hu.bsstudio.raktr.model;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rents")
@JsonSerialize()
@JsonDeserialize(builder = Rent.RentBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(targetEntity = RentItem.class, fetch = LAZY, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<RentItem> rentItems;

    @SuppressWarnings("checkstyle:DesignForExtension")
    public RentItem getRentItemOfScannable(final Scannable scannableToFind) {
        for (RentItem rentItem : rentItems) {
            if (rentItem.getScannable().getId().equals(scannableToFind.getId())) {
                return rentItem;
            }
        }

        return null;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentBuilder {}
}
