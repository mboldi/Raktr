package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.service.CategoryService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/category")
public final class CategoryController {

    private transient CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public Category createCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request for new category: {}", categoryRequest);
        return categoryService.create(categoryRequest);
    }

    @GetMapping
    public List<Category> categoryList() {
        log.info("Incoming request for all categories");
        return categoryService.getAll();
    }

    @PutMapping
    public Category updateCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request for updating a category: {}", categoryRequest);
        return categoryService.update(categoryRequest);
    }

    @DeleteMapping
    public Category deleteCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request to delete category: {}", categoryRequest);
        return categoryService.delete(categoryRequest);
    }
}
