package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Device.class, name = "device"),
    @JsonSubTypes.Type(value = CompositeItem.class, name = "compositeItem")
})
@SuppressWarnings("checkstyle:VisibilityModifier")
public abstract class Scannable extends DomainAuditModel {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotBlank
    protected String name;

    @NotBlank
    @Column(unique = true)
    protected String textIdentifier;

    @NotNull
    @Column(unique = true)
    protected String barcode;

    @NotNull
    protected Boolean isPublicRentable;

    @NotNull
    protected Boolean isDeleted;

    @NotNull
    @ManyToOne
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Location location;

    @NotNull
    @JoinColumn
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Category category;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "scannable")
    List<RentItem> rentItems;

    public Scannable setDeletedData() {
        barcode = barcode + "_d_" + id;
        textIdentifier = textIdentifier + "_d_" + id;
        isDeleted = true;

        return this;
    }

    public Scannable setUndeletedData() {
        barcode = barcode.split("_")[0];
        textIdentifier = textIdentifier.split("_")[0];
        isDeleted = false;

        return this;
    }
}
