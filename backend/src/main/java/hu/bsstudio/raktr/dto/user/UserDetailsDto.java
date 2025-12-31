package hu.bsstudio.raktr.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {

    private UUID uuid;

    private String username;

    private String familyName;

    private String givenName;

    private String nickname;

    private String personalId;

    private Set<String> roles;

}
