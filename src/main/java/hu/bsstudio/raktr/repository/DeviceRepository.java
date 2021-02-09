package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device, Long> {

    String FIND_BY_BARCODE_QUERY = "SELECT device FROM Device device WHERE device.barcode = :barcode";

    @Query(FIND_BY_BARCODE_QUERY)
    Optional<Device> findByBarcode(@Param("barcode") String barcode);
}
