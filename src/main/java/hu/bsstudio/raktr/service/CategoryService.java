package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public final Category create(final Category category) {
        final Category saved = categoryRepository.save(category);
        log.info("Category created: {}", saved);
        return saved;
    }

    public final List<Category> getAll() {
        List<Category> fetched = categoryRepository.findAll();
        log.info("Categories fetched from DB: {}", fetched);
        return fetched;
    }

    public final Category update(final Category categoryRequest) {
        final Category categoryToUpdate = categoryRepository.findById(categoryRequest.getId()).orElse(null);

        if (categoryToUpdate == null) {
            throw new ObjectNotFoundException();
        }

        categoryToUpdate.setName(categoryRequest.getName());

        final Category updated = categoryRepository.save(categoryToUpdate);
        log.info("Category updated in DB: {}", updated);
        return updated;
    }

    public final Category delete(final Category categoryRequest) {
        categoryRepository.delete(categoryRequest);
        log.info("Category deleted: {}", categoryRequest);
        return categoryRequest;
    }
}
