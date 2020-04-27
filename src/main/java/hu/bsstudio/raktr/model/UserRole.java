package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_role")
@JsonSerialize
@JsonDeserialize(builder = UserRole.Builder.class)
@NoArgsConstructor
@Data
public class UserRole {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String roleName;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

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
