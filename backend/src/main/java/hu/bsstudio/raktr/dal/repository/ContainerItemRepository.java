package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.ContainerItem;
import hu.bsstudio.raktr.dal.entity.ContainerItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerItemRepository extends JpaRepository<ContainerItem, ContainerItemId> {

}
