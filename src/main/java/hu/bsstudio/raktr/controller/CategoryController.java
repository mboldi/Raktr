package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public Category createCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request for new category: {}", categoryRequest);
        return categoryService.create(categoryRequest);
    }
}
