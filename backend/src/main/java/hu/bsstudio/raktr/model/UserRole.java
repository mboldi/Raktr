package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "user_role")
@JsonSerialize
@JsonDeserialize(builder = UserRole.Builder.class)
@NoArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
public class UserRole {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @ToString.Include
    @Column(unique = true)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<User> users;

    public UserRole(final Builder builder) {
        this.id = builder.id;
        this.roleName = builder.roleName;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String roleName;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withRoleName(final String roleName) {
            this.roleName = roleName;
            return this;
        }

        public UserRole build() {
            return new UserRole(this);
        }
    }
}
