package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> categories);
}
