package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.user.service.UserSyncService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class BssLoginJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserSyncService userSyncService;

    @Override
    public Collection<GrantedAuthority> convert(final @NonNull Jwt jwt) {
        return userSyncService.syncUserFromJwt(jwt).getAuthorities();
    }

}
