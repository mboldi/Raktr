package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "locations")
@JsonSerialize
@JsonDeserialize(builder = Location.Builder.class)
@NoArgsConstructor
@Data
public final class Location extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Device.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "location")
    private Set<Device> devices;

    public Location(final Builder builder) {
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private String name;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Location build() {
            return new Location(this);
        }
    }
}
