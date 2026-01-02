package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dal.entity.Location;
import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByBarcode(String barcode);

    Optional<Device> findByTextIdentifier(String textIdentifier);

    boolean existsByCategory(Category category);

    boolean existsByLocation(Location location);

    boolean existsByOwner(Owner owner);

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
