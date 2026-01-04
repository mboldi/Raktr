package hu.bsstudio.raktr.category.mapper;

import hu.bsstudio.raktr.dal.entity.Category;
import hu.bsstudio.raktr.dto.category.CategoryCreateDto;
import hu.bsstudio.raktr.dto.category.CategoryDetailsDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateTimeMapper.class)
public interface CategoryMapper {

    CategoryDetailsDto entityToDetailsDto(Category category);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Category createDtoToEntity(CategoryCreateDto createDto);

}
