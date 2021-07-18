package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
