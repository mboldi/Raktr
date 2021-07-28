package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "owner")
@JsonSerialize
@JsonDeserialize(builder = Owner.OwnerBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Owner extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    private boolean inSchInventory;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @OneToMany(targetEntity = Device.class, fetch = FetchType.LAZY)
    private List<Device> devices;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OwnerBuilder {}
}
