package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.UserRole;
import hu.bsstudio.raktr.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getRole(String roleName) {
        return userRoleRepository.findByRoleName("ROLE_" + roleName).orElse(null);
    }

}
