package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.CompositeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CompositeItemDao extends JpaRepository<CompositeItem, Long> {
}