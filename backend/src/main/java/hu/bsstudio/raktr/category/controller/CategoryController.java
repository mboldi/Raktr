package hu.bsstudio.raktr.category.controller;

import hu.bsstudio.raktr.category.service.CategoryService;
import hu.bsstudio.raktr.dto.category.CategoryCreateDto;
import hu.bsstudio.raktr.dto.category.CategoryDetailsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDetailsDto> listCategories() {
        return categoryService.listCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetailsDto createCategory(@RequestBody @Valid CategoryCreateDto createDto) {
        return categoryService.createCategory(createDto);
    }

    @DeleteMapping("/{categoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String categoryName) {
        categoryService.deleteCategory(categoryName);
    }

}
