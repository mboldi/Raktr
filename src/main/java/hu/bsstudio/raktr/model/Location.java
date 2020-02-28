package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "location")
@JsonSerialize
@JsonDeserialize(builder = Location.Builder.class)
@NoArgsConstructor
@Data
public class Location extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Device.class, fetch = FetchType.EAGER,
        cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true, mappedBy = "location")
    private Set<Device> devices;

    public Location(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String name;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Location build() {
            return new Location(this);
        }
    }
}
