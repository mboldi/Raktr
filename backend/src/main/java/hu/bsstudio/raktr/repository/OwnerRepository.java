package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    String FIND_BY_NAME_QUERY = "SELECT owner FROM Owner owner WHERE owner.name = :name";

    @Query(FIND_BY_NAME_QUERY)
    Optional<Owner> findByName(@Param("name") String name);
}
