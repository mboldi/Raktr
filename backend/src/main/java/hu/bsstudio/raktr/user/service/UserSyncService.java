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

    // The cache must wrap the transaction; RaktrApplication's @EnableCaching pins that order.
    @Cacheable(value = "usersByUuid", key = "#jwt.getClaimAsString('sub')", sync = true)
    @Transactional
    public User syncUserFromJwt(Jwt jwt) {
        final UUID uuid = UUID.fromString(Objects.requireNonNull(jwt.getClaimAsString("sub")));
        final String username = jwt.getClaimAsString("preferred_username");
        final String familyName = jwt.getClaimAsString("family_name");
        final String givenName = jwt.getClaimAsString("given_name");

        if (userRepository.insertIfAbsent(uuid, username, familyName, givenName) > 0) {
            log.info("Created new user [{}] with UUID [{}].", username, uuid);
        }

        User user = userRepository.findById(uuid).orElseThrow();

        user.setFamilyName(familyName);
        user.setGivenName(givenName);

        @SuppressWarnings("unchecked")
        List<String> groups = (List<String>) jwt.getClaims().getOrDefault("groups", List.of());
        Set<String> normalizedGroups = groups.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        user.setGroups(normalizedGroups);

        return user;
    }

}
