package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.service.CategoryService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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
}
