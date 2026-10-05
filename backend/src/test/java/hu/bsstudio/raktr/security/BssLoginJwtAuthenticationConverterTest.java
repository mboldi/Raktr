package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.user.service.UserSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BssLoginJwtAuthenticationConverterTest {

    private final UserSyncService userSyncService = mock(UserSyncService.class);

    private final BssLoginJwtAuthenticationConverter converter = new BssLoginJwtAuthenticationConverter(userSyncService);

    @Test
    void returnsAuthoritiesOfSyncedUser() {
        var jwt = mock(Jwt.class);
        var user = new User();
        user.setGroups(Set.of("admin"));
        when(userSyncService.syncUserFromJwt(jwt)).thenReturn(user);

        assertThat(converter.convert(jwt))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(RoleConstants.ADMIN);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void rejectsNullJwt() {
        assertThatThrownBy(() -> converter.convert(null)).isInstanceOf(NullPointerException.class);
        verifyNoInteractions(userSyncService);
    }

}
