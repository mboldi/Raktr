package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityServiceTest {

    private final SecurityService securityService = new SecurityService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsCurrentUserUuid() {
        var uuid = UUID.randomUUID();
        authenticate(uuid.toString());

        assertThat(securityService.getCurrentUserUuid()).isEqualTo(uuid);
    }

    @Test
    void rejectsSubjectThatIsNotUuid() {
        authenticate("not-a-uuid");

        assertThatThrownBy(securityService::getCurrentUserUuid)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Authenticated user has no valid UUID identifier!");
    }

    @Test
    void rejectsMissingAuthentication() {
        assertThatThrownBy(securityService::getCurrentUserUuid)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No authentication found!");
        assertThatThrownBy(securityService::isAdmin)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No authentication found!");
    }

    @Test
    void allowsAdminToActOnOtherUsers() {
        authenticate(UUID.randomUUID().toString(), RoleConstants.ADMIN);

        assertThat(securityService.isAdmin()).isTrue();
        assertThatCode(() -> securityService.checkIsOwnerOrAdmin(UUID.randomUUID())).doesNotThrowAnyException();
    }

    @Test
    void allowsOwnerAndRejectsOthers() {
        var uuid = UUID.randomUUID();
        authenticate(uuid.toString(), RoleConstants.MEMBER);

        assertThat(securityService.isAdmin()).isFalse();
        assertThatCode(() -> securityService.checkIsOwnerOrAdmin(uuid)).doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.checkIsOwnerOrAdmin(UUID.randomUUID()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to perform this action!");
    }

    private static void authenticate(String subject, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(subject, null, authorities));
    }

}
