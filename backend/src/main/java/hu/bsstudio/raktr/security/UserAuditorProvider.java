package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.dal.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserAuditorProvider implements AuditorAware<User> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> getCurrentAuditor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        try {
            var uuid = UUID.fromString(authentication.getName());
            return Optional.of(entityManager.getReference(User.class, uuid));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for authenticated user: {}", authentication.getName());
            return Optional.empty();
        }
    }

}
