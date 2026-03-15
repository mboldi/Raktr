package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Rent;
import hu.bsstudio.raktr.dal.entity.RentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface RentItemRepository extends JpaRepository<RentItem, Long> {

    Optional<RentItem> findByIdAndRent(Long id, Rent rent);

    @Query("""
            SELECT COALESCE(SUM(ri.quantity), 0)
            FROM RentItem ri
            JOIN ri.rent r
            WHERE ri.scannable.id = :scannableId
                AND r.deleted = false
                AND ri.status <> 'RETURNED'
                AND r.outDate <= :endDate
                AND COALESCE(r.actualReturnDate, r.expectedReturnDate) >= :startDate
                AND (:excludeRentItemId IS NULL OR ri.id <> :excludeRentItemId)
            """)
    int sumBookedQuantity(Long scannableId, LocalDate startDate, LocalDate endDate, Long excludeRentItemId);

}
