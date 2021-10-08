package hu.bsstudio.raktr.model;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

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

    @NotNull
    private RentType type;

    private String destination;

    @ManyToOne(fetch = LAZY)
    private User issuer;

    @NotBlank
    private String renter;

    @NotNull
    private Date outDate;

    private Date backDate;

    @NotNull
    private Boolean isClosed;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(targetEntity = RentItem.class, fetch = LAZY, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<RentItem> rentItems;

    @ManyToMany
    private List<Comment> comments;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = LAZY, targetEntity = Project.class)
    private Project project;

    @JsonIgnore
    private Boolean isDeleted;

    @SuppressWarnings("checkstyle:DesignForExtension")
    public RentItem getRentItemOfScannable(final Scannable scannableToFind) {
        for (RentItem rentItem : rentItems) {
            if (rentItem.getScannable().getId().equals(scannableToFind.getId())) {
                return rentItem;
            }
        }

        return null;
    }

    public Rent setDeletedData() {
        isDeleted = true;

        return this;
    }

    public Rent setUndeletedData() {
        isDeleted = false;

        return this;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentBuilder {}
}
