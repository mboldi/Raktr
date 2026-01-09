package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    boolean existsByName(String name);

}
