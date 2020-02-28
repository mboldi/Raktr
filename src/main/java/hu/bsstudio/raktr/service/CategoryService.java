package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.CategoryDao;
import hu.bsstudio.raktr.model.Category;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService(final CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Category create(final Category category) {
        final Category saved = categoryDao.save(category);
        log.info("Category created: {}", saved);
        return saved;
    }

    public List<Category> getAll() {
        List<Category> fetched = categoryDao.findAll();
        log.info("Categories fetched from DB: {}", fetched);
        return fetched;
    }

    public Category update(final Category categoryRequest) {
        final Category categoryToUpdate = categoryDao.getOne(categoryRequest.getId());
        categoryToUpdate.setName(categoryRequest.getName());
        final Category updated = categoryDao.save(categoryToUpdate);
        log.info("Category updated in DB: {}", updated);
        return updated;
    }

    public Category delete(final Category categoryRequest) {
        categoryDao.delete(categoryRequest);
        log.info("Category deleted: {}", categoryRequest);
        return categoryRequest;
    }
}
