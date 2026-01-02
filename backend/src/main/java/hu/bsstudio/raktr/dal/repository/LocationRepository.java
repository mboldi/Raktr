package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {
}
