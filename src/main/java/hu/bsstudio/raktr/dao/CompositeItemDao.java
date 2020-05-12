package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.CompositeItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CompositeItemDao extends JpaRepository<CompositeItem, Long> {

    String FIND_BY_BARCODE_QUERY = "SELECT compositeItem FROM CompositeItem compositeItem WHERE compositeItem.barcode = :barcode";

    @Query(FIND_BY_BARCODE_QUERY)
    Optional<CompositeItem> findByBarcode(@Param("barcode") String barcode);
}
