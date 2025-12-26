package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByName(String name);

}
