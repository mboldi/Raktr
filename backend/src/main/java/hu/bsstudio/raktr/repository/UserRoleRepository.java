package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByRoleName(String roleName);

}
