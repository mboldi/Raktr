package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    String FIND_BY_USERNAME_QUERY = "SELECT user FROM User user WHERE user.username = :username";

    @Query(FIND_BY_USERNAME_QUERY)
    Optional<User> findByUsername(@Param("username") String username);
}
