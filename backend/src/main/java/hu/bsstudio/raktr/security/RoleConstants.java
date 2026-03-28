package hu.bsstudio.raktr.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ALUMNI = "ROLE_ALUMNI";

    public static final String MEMBER = "ROLE_MEMBER";

    public static final String MEMBER_CANDIDATE = "ROLE_MEMBER_CANDIDATE";

    public static final String MEMBER_CANDIDATE_CANDIDATE = "ROLE_MEMBER_CANDIDATE_CANDIDATE";

    private static final Map<String, String> SSO_TO_APP_ROLE = Map.of(
            "ADMIN", ADMIN,
            "ÖREGTAG", ALUMNI,
            "STÚDIÓS", MEMBER,
            "STÚDIÓS JELÖLT", MEMBER_CANDIDATE,
            "STÚDIÓS JELÖLT-JELÖLT", MEMBER_CANDIDATE_CANDIDATE
    );

    public static Optional<String> fromSso(String ssoRole) {
        return Optional.ofNullable(SSO_TO_APP_ROLE.get(ssoRole.toUpperCase()));
    }

}
