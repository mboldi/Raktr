package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.DeviceRentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRentItemDao extends JpaRepository<DeviceRentItem, Long> {
}
