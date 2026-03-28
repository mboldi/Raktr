package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
