package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    String FIND_BY_ROLENAME_QUERY = "SELECT userRole FROM UserRole userRole WHERE userRole.roleName = :rolename";

    @Query(FIND_BY_ROLENAME_QUERY)
    Optional<UserRole> findByRoleName(@Param("rolename") String rolename);
}
