package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "projects")
@JsonSerialize()
@JsonDeserialize(builder = Project.ProjectBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Project extends DomainAuditModel{

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    private User prodManager;

    @NotNull
    private Date startDate;

    private Date expEndDate;

    @OneToMany(targetEntity = Rent.class, fetch = LAZY)
    @Setter(AccessLevel.NONE)
    private List<Rent> rents;

    @JsonIgnore
    private Boolean isDeleted;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ProjectBuilder {}

    public Project setDeletedData() {
        isDeleted = true;

        return this;
    }

    public Project setUndeletedData() {
        isDeleted = false;

        return this;
    }
}
