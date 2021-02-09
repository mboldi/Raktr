package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Location;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LocationRepository extends JpaRepository<Location, Long> {

    String FIND_BY_NAME_QUERY = "SELECT location FROM Location location WHERE location.name = :name";

    @Query(FIND_BY_NAME_QUERY)
    Optional<Location> findByName(@Param("name") String name);
}
