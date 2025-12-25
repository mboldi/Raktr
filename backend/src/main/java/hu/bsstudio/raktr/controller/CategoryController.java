package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/category")
public class CategoryController {

    private static final int CONFLICT = 409;

    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request for new category: {}", categoryRequest);

        final var createdCategory = categoryService.create(categoryRequest);

        return createdCategory
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @GetMapping
    public ResponseEntity<List<Category>> categoryList() {
        log.info("Incoming request for all categories");
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PutMapping
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request for updating a category: {}", categoryRequest);

        Optional<Category> updatedCategory = categoryService.update(categoryRequest);

        return updatedCategory
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<Category> deleteCategory(@Valid @RequestBody final Category categoryRequest) {
        log.info("Incoming request to delete category: {}", categoryRequest);

        final var deletedCategory = categoryService.delete(categoryRequest);

        return deletedCategory
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
