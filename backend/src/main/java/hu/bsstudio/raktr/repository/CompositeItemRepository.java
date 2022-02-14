package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CompositeItemRepository extends JpaRepository<CompositeItem, Long> {
    Optional<CompositeItem> findByBarcode(String barcode);

    Optional<CompositeItem> findByTextIdentifier(String textIdentifier);

    @Override
    @Query(value = "SELECT compositeItem FROM CompositeItem compositeItem where compositeItem.isDeleted = false")
    List<CompositeItem> findAll();

    @Query(value = "SELECT compositeItem FROM CompositeItem compositeItem where compositeItem.isDeleted = true")
    List<CompositeItem> findAllDeleted();
}
