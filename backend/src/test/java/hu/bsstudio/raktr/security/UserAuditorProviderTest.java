package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.dal.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserAuditorProviderTest {

    private final EntityManager entityManager = mock(EntityManager.class);

    private final UserAuditorProvider provider = new UserAuditorProvider();

    @BeforeEach
    void injectEntityManager() {
        ReflectionTestUtils.setField(provider, "entityManager", entityManager);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsReferenceToAuthenticatedUser() {
        var uuid = UUID.randomUUID();
        var user = new User();
        when(entityManager.getReference(User.class, uuid)).thenReturn(user);
        setAuthentication(new TestingAuthenticationToken(uuid.toString(), null, RoleConstants.ADMIN));

        assertThat(provider.getCurrentAuditor()).containsSame(user);
    }

    @Test
    void returnsEmptyWithoutAuthentication() {
        assertThat(provider.getCurrentAuditor()).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    void returnsEmptyForUnauthenticatedToken() {
        setAuthentication(new TestingAuthenticationToken(UUID.randomUUID().toString(), null));

        assertThat(provider.getCurrentAuditor()).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    void returnsEmptyForAnonymousToken() {
        setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThat(provider.getCurrentAuditor()).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    void returnsEmptyForSubjectThatIsNotUuid() {
        setAuthentication(new TestingAuthenticationToken("not-a-uuid", null, RoleConstants.ADMIN));

        assertThat(provider.getCurrentAuditor()).isEmpty();
        verifyNoInteractions(entityManager);
    }

    private static void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
