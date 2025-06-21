package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.UserRole;
import hu.bsstudio.raktr.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getRole(String roleName) {
        return userRoleRepository.findByRoleName("ROLE_" + roleName).orElse(null);
    }

    public UserRole createRole(String roleName) {
        Optional<UserRole> foundRole = userRoleRepository.findByRoleName("ROLE_" + roleName);

        if(foundRole.isPresent()) {
            return foundRole.get();
        }

        UserRole userRole = new UserRole.Builder().withRoleName("ROLE_" + roleName).build();

        return userRoleRepository.save(userRole);
    }

}
