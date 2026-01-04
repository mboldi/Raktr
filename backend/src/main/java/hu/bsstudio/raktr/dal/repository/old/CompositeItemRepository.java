package hu.bsstudio.raktr.dal.repository.old;

import hu.bsstudio.raktr.model.CompositeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompositeItemRepository extends JpaRepository<CompositeItem, Long> {

    Optional<CompositeItem> findByBarcode(String barcode);

    Optional<CompositeItem> findByTextIdentifier(String textIdentifier);

    @Override
    @Query(value = "SELECT compositeItem FROM CompositeItem compositeItem where compositeItem.isDeleted = false")
    List<CompositeItem> findAll();

    @Query(value = "SELECT compositeItem FROM CompositeItem compositeItem where compositeItem.isDeleted = true")
    List<CompositeItem> findAllDeleted();

}
