package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Rent;
import hu.bsstudio.raktr.dal.entity.RentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface RentItemRepository extends JpaRepository<RentItem, Long> {

    Optional<RentItem> findByIdAndRent(Long id, Rent rent);

    boolean existsByRentAndScannableId(Rent rent, Long scannableId);

    // Counts the device both when booked directly and when booked inside a container.
    @Query("""
            SELECT COALESCE(SUM(ri.quantity * COALESCE(ci.quantity, 1)), 0)
            FROM RentItem ri
            JOIN ri.rent r
            LEFT JOIN ContainerItem ci ON ci.container.id = ri.scannable.id AND ci.device.id = :deviceId
            WHERE (ri.scannable.id = :deviceId OR ci.device.id = :deviceId)
                AND r.id <> :excludeRentId
                AND r.deleted = false
                AND r.outDate <= :endDate
                AND ri.status <> 'RETURNED'
                AND COALESCE(r.actualReturnDate, r.expectedReturnDate) >= :startDate
            """)
    int sumBookedQuantityExcludingRent(Long deviceId, LocalDate startDate, LocalDate endDate, Long excludeRentId);

}
