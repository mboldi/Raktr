package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    @Query(value = "SELECT project FROM Project project where project.isDeleted = false")
    List<Project> findAll();

    @Query(value = "SELECT project FROM Project project where project.isDeleted = true ")
    List<Project> findAllDeleted();
}
