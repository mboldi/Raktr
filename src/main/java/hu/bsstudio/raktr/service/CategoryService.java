package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public final Optional<Category> create(final Category category) {
        final var byName = categoryRepository.findByName(category.getName());

        if (byName.isEmpty()) {
            final Category saved = categoryRepository.save(category);
            log.info("Category created: {}", saved);
            return Optional.of(saved);
        } else {
            return Optional.empty();
        }
    }

    public final List<Category> getAll() {
        List<Category> fetched = categoryRepository.findAll();
        log.info("Categories fetched from DB: {}", fetched);
        return fetched;
    }

    public final Optional<Category> update(final Category categoryRequest) {
        final var categoryToUpdate = categoryRepository.findById(categoryRequest.getId());

        if (categoryToUpdate.isEmpty()) {
            return Optional.empty();
        }

        categoryToUpdate.get().setName(categoryRequest.getName());

        final Category updated = categoryRepository.save(categoryToUpdate.get());
        log.info("Category updated in DB: {}", updated);
        return Optional.of(updated);
    }

    public final Optional<Category> delete(final Category categoryRequest) {
        final var foundCategory = categoryRepository.findById(categoryRequest.getId());

        if (foundCategory.isPresent()) {
            categoryRepository.delete(categoryRequest);
            log.info("Category deleted: {}", categoryRequest);
        } else {
            log.info("Category not found to delete with id: {}", categoryRequest.getId());
        }

        return foundCategory;
    }
}
