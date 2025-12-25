package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByName(String name);

}
