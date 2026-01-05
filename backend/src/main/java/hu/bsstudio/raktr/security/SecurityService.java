package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    public String getCurrentUsername() {
        return getAuthentication().getName();
    }

    public UUID getCurrentUserUuid() {
        try {
            return UUID.fromString(getCurrentUsername());
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Authenticated user has no valid UUID identifier!");
        }
    }

    public boolean isAdmin() {
        return getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> RoleConstants.ADMIN.equals(grantedAuthority.getAuthority()));
    }

    public void checkIsOwnerOrAdmin(UUID ownerUuid) {
        if (!isAdmin() && !getCurrentUserUuid().equals(ownerUuid)) {
            throw new AccessDeniedException("You do not have permission to perform this action!");
        }
    }

    private Authentication getAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No authentication found!");
        }
        return authentication;
    }

}
