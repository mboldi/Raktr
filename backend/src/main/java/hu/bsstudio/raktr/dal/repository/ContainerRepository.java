package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    List<Container> findAllByDeleted(boolean deleted);

}
