package hu.bsstudio.raktr.user.service;

import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    @Cacheable(value = "usersByUuid", key = "#jwt.getClaimAsString('sub')")
    @Transactional
    public User syncUserFromJwt(Jwt jwt) {
        final UUID uuid = UUID.fromString(jwt.getClaimAsString("sub"));
        final String username = jwt.getClaimAsString("preferred_username");

        User user = userRepository.findById(uuid)
                .orElseGet(() -> {
                    log.info("User with UUID [{}] not found. Creating new user for [{}].", uuid, username);
                    var newUser = new User();
                    newUser.setUuid(uuid);
                    newUser.setUsername(username);
                    return newUser;
                });

        user.setFamilyName(jwt.getClaimAsString("family_name"));
        user.setGivenName(jwt.getClaimAsString("given_name"));

        @SuppressWarnings("unchecked")
        List<String> groups = (List<String>) jwt.getClaims().getOrDefault("groups", List.of());
        Set<String> normalizedGroups = groups.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        user.setGroups(normalizedGroups);

        return userRepository.save(user);
    }

}
