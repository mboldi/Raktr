package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.CategoryDao;
import hu.bsstudio.raktr.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService(final CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public final Category create(final Category category) {
        final Category saved = categoryDao.save(category);
        log.info("Category created: {}", saved);
        return saved;
    }
}
