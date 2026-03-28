package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findAllByDeleted(boolean deleted);

    @Query("SELECT DISTINCT d.manufacturer FROM Device d WHERE d.manufacturer IS NOT NULL ORDER BY d.manufacturer")
    List<String> findDistinctManufacturers();

}
