package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface LocationDao extends JpaRepository<Location, Long> {

    String FIND_BY_NAME_QUERY = "SELECT location FROM Location location WHERE location.name = :name";

    @Query(FIND_BY_NAME_QUERY)
    Optional<Location> findByName(@Param("name") String name);
}
