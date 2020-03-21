package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@JsonSerialize
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("checkstyle:VisibilityModifier")
public abstract class Scannable extends DomainAuditModel {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    String name;

    @NotNull
    @Column(unique = true)
    String barcode;
}
