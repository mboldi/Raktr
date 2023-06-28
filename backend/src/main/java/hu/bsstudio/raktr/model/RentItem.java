package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

import static javax.persistence.FetchType.*;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("checkstyle:FinalClass")
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
    @ManyToOne
    @JoinColumn(name = "rent_id")
    private Rent rent;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentItemBuilder {}
}
