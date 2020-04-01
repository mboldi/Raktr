package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.RentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentItemDao extends JpaRepository<RentItem, Long> {
}
