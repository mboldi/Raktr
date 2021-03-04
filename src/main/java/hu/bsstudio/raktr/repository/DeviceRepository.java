package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByBarcode(String barcode);

    Optional<Device> findByTextIdentifier(String textIdentifier);

    @Query(value = "SELECT max(id) FROM Scannable")
    long getMaxId();
}
