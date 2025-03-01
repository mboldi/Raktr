package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface RentItemRepository extends JpaRepository<RentItem, Long> {

    Optional<List<RentItem>> findRentItemsByScannable_Id(Long id);

    @Query("SELECT rent from Rent rent, RentItem rentItem " +
            "WHERE rentItem.id = :rentItemId " +
            "AND rent.id = rentItem.rent.id")
    Optional<Rent> findRentOfRentItem(Long rentItemId);
}
