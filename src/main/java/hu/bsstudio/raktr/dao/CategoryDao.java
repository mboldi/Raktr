package hu.bsstudio.raktr.dao;

import hu.bsstudio.raktr.model.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CategoryDao extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
