package hu.bsstudio.raktr.security;

import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.protocol.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SentryUserEventProcessor implements EventProcessor {

    @Override
    public SentryEvent process(SentryEvent event, Hint hint) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = (Jwt) jwtAuth.getPrincipal();
            var user = new User();
            user.setUsername(jwt.getClaimAsString("preferred_username"));
            user.setId(jwt.getClaimAsString("sub"));
            event.setUser(user);
        }
        return event;
    }

}
