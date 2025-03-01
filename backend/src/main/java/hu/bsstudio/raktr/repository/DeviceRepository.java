package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Device;
import java.util.List;
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

    @Query("SELECT DISTINCT d.maker FROM Device d")
    List<String> findAllMakers();

    @Query(value = "SELECT max(id) FROM Scannable")
    long getMaxId();

    @Override
    @Query(value = "SELECT device FROM Device device where device.isDeleted = false")
    List<Device> findAll();

    @Query(value = "SELECT device FROM Device device where device.isDeleted = true")
    List<Device> findAllDeleted();
}
