package hu.bsstudio.raktr.category.service;

import hu.bsstudio.raktr.category.mapper.CategoryMapper;
import hu.bsstudio.raktr.dal.repository.CategoryRepository;
import hu.bsstudio.raktr.dal.repository.DeviceRepository;
import hu.bsstudio.raktr.dto.category.CategoryCreateDto;
import hu.bsstudio.raktr.dto.category.CategoryDetailsDto;
import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
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

    private final DeviceRepository deviceRepository;

    private final CategoryMapper categoryMapper;

    public List<CategoryDetailsDto> listCategories() {
        var categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public CategoryDetailsDto createCategory(CategoryCreateDto createDto) {
        createDto.setName(createDto.getName().trim());

        if (categoryRepository.existsById(createDto.getName())) {
            throw new ObjectConflictException();
        }

        var category = categoryMapper.createDtoToEntity(createDto);

        category = categoryRepository.saveAndFlush(category);

        log.info("Created Category with name [{}]", category.getName());

        return categoryMapper.entityToDetailsDto(category);
    }

    @Transactional
    public void deleteCategory(String categoryName) {
        var category = categoryRepository.findById(categoryName)
                .orElseThrow(ObjectNotFoundException::new);

        if (deviceRepository.existsByCategory(category)) {
            throw new ObjectConflictException();
        }

        categoryRepository.delete(category);

        log.info("Deleted Category with name [{}]", category.getName());
    }

}
