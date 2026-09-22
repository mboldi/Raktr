package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @Modifying
    @Query(value = """
            INSERT INTO users (uuid, username, family_name, given_name)
            VALUES (:uuid, :username, :familyName, :givenName)
            ON CONFLICT (uuid) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(UUID uuid, String username, String familyName, String givenName);

}
