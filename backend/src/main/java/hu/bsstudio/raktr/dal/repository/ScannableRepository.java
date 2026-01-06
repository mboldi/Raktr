package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.dal.entity.Scannable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScannableRepository extends JpaRepository<Scannable, Long> {

    Optional<Scannable> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    boolean existsByCategory(Category category);

    boolean existsByLocation(Location location);

    boolean existsByOwner(Owner owner);

}
