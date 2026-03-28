package hu.bsstudio.raktr.dal.entity;

import hu.bsstudio.raktr.security.RoleConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @EqualsAndHashCode.Include
    private UUID uuid;

    @ToString.Include
    private String username;

    @ToString.Include
    private String familyName;

    @ToString.Include
    private String givenName;

    private String nickname;

    private String personalId;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> groups;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (groups == null) {
            return Collections.emptySet();
        }
        return groups.stream()
                .map(RoleConstants::fromSso)
                .flatMap(Optional::stream)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return null;
    }

    public boolean hasAnyAuthority(final Collection<String> authorities) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> authorities.contains(grantedAuthority.getAuthority()));
    }

}
