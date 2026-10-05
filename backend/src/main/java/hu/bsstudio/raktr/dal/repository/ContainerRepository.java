package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Container;
import hu.bsstudio.raktr.dal.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    List<Container> findAllByDeleted(boolean deleted);

    List<Container> findAllByItemsDeviceOrderById(Device device);

}
