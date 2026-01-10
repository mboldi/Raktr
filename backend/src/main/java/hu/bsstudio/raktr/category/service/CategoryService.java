package hu.bsstudio.raktr.category.service;

import hu.bsstudio.raktr.category.mapper.CategoryMapper;
import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.CategoryRepository;
import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.dto.category.CategoryCreateDto;
import hu.bsstudio.raktr.dto.category.CategoryDetailsDto;
import hu.bsstudio.raktr.exception.EntityAlreadyExistsException;
import hu.bsstudio.raktr.exception.EntityInUseException;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ScannableRepository scannableRepository;

    private final CategoryMapper categoryMapper;

    public List<CategoryDetailsDto> listCategories() {
        var categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public CategoryDetailsDto createCategory(CategoryCreateDto createDto) {
        createDto.setName(createDto.getName().trim());

        if (categoryRepository.existsById(createDto.getName())) {
            throw new EntityAlreadyExistsException(Category.class, createDto.getName());
        }

        var category = categoryMapper.createDtoToEntity(createDto);

        category = categoryRepository.saveAndFlush(category);

        log.info("Created Category with name [{}]", category.getName());

        return categoryMapper.entityToDetailsDto(category);
    }

    @Transactional
    public void deleteCategory(String categoryName) {
        var category = categoryRepository.findById(categoryName)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryName));

        if (scannableRepository.existsByCategory(category)) {
            throw new EntityInUseException(Category.class, categoryName, Scannable.class);
        }

        categoryRepository.delete(category);

        log.info("Deleted Category with name [{}]", category.getName());
    }

}
