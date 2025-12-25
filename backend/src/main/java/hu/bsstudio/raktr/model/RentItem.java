package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.bsstudio.raktr.dal.value.BackStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonSerialize
@JsonDeserialize(builder = RentItem.RentItemBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentItem extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull
    @JoinColumn
    @ManyToOne(fetch = EAGER)
    private Scannable scannable;

    @NotNull
    private BackStatus backStatus;

    @NotNull
    private Integer outQuantity;

    @NotNull
    private Date addedAt;

    @ManyToOne(targetEntity = User.class, fetch = LAZY)
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "addedByUserId")
    private User addedBy;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne(targetEntity = Rent.class, fetch = LAZY)
    @JoinColumn(name = "rent_id")
    private Rent rent;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentItemBuilder {}
}
