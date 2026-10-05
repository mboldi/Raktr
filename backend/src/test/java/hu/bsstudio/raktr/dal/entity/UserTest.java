package hu.bsstudio.raktr.dal.entity;

import hu.bsstudio.raktr.security.RoleConstants;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void returnsNoAuthoritiesWhenGroupsAreNull() {
        var user = new User();

        assertThat(user.getAuthorities()).isEmpty();
        assertThat(user.hasAnyAuthority(List.of(RoleConstants.ADMIN))).isFalse();
    }

    @Test
    void mapsKnownSsoGroupsToAuthoritiesAndIgnoresUnknownOnes() {
        var user = new User();
        user.setGroups(Set.of("admin", "unknown"));

        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(RoleConstants.ADMIN);
        assertThat(user.hasAnyAuthority(List.of(RoleConstants.ADMIN))).isTrue();
    }

    @Test
    void hasNoPassword() {
        assertThat(new User().getPassword()).isNull();
    }

}
