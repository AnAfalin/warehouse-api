package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.repository.CategoryRepository;
import ru.lazarenko.warehouse.service.mapper.CategoryMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService underTest;

    @MockBean
    CategoryRepository categoryRepository;

    @MockBean
    CategoryMapper categoryMapper;

    Category category1;
    Category category2;

    CategoryDto categoryResponseDto1;
    CategoryDto categoryResponseDto2;

    CategoryDto categoryRequestDto1;
    CategoryDto categoryRequestDto2;

    @BeforeEach
    void prepare() {
        category1 = Category.builder().id(1).name("coffee").build();
        category2 = Category.builder().id(2).name("tea").build();

        categoryResponseDto1 = CategoryDto.builder().id(1).name("coffee").build();
        categoryResponseDto2 = CategoryDto.builder().id(2).name("tea").build();

        categoryRequestDto1 = CategoryDto.builder().name("coffee").build();
        categoryRequestDto2 = CategoryDto.builder().name("tea").build();
    }

    @Test
    @DisplayName("create category | NoUniqueObjectException | name is not unique")
    void createCategory_noUniqueObjectException_nameIsNotUnique() {
        doThrow(NoUniqueObjectException.class)
                .when(categoryRepository)
                .findByName(anyString());

        assertThrows(NoUniqueObjectException.class, () -> underTest.createCategory(categoryRequestDto1));
    }

    @Test
    @DisplayName("create category | successful created | name is unique")
    void createCategory_successfulCreated_nameIsNotUnique() {
        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(categoryMapper.toCategory(any(CategoryDto.class))).thenReturn(category1);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category1);

        ResponseDto result = underTest.createCategory(categoryRequestDto1);

        verify(categoryRepository, times(1))
                .save(any(Category.class));
        assertThat(result.getMessage()).isEqualTo("Category successful created: id='1'");
    }

    @Test
    @DisplayName("get all categories | result list is empty | categories do not exist")
    void getAllCategories_resultListIsEmpty_regionsDoNotExist() {
        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryDto> result = underTest.getAllCategories();
        verify(categoryRepository, times(1))
                .findAll();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("get all categories | result list is not empty | categories exist")
    void getAllCategories_resultListIsNotEmpty_regionsExist() {
        List<Category> categories = List.of(category1, category2);
        List<CategoryDto> categoryDtos = List.of(categoryResponseDto1, categoryResponseDto2);

        when(categoryRepository.findAll())
                .thenReturn(categories);

        when(categoryMapper.toCategoryDtoList(anyList()))
                .thenReturn(categoryDtos);

        List<CategoryDto> result = underTest.getAllCategories();
        verify(categoryRepository, times(1))
                .findAll();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getId()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("coffee"),
                () -> assertThat(result.get(1).getId()).isEqualTo(2),
                () -> assertThat(result.get(1).getName()).isEqualTo("tea")
        );
    }

    @Test
    @DisplayName("check unique name | NoUniqueObjectException | category name is not unique")
    void checkUniqueName_noUniqueObjectException_regionNameIsNotUnique() {
        String name = "Sochi";

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(category2));

        assertThrows(NoUniqueObjectException.class, () -> underTest.checkUniqueName(name));
    }

    @Test
    @DisplayName("check unique name | optional is empty and no exception | category name is unique")
    void checkUniqueName_optionalIsEmptyAndNoException_regionNameIsUnique() {
        String name = "Krasnodar";

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        underTest.checkUniqueName(name);

        verify(categoryRepository, times(1))
                .findByName(anyString());
    }

    @Test
    @DisplayName("check exist and get category by name | NoFoundElementException | category does not exist")
    void checkExistAndGetCategoryByName_noFoundElementException_categoryDoesNotExist() {
        String name = "books";

        doThrow(NoUniqueObjectException.class)
                .when(categoryRepository)
                .findByName(anyString());

        assertThrows(NoUniqueObjectException.class, () -> underTest.checkExistAndGetCategoryByName(name));
    }

    @Test
    @DisplayName("check exist and get category by name | correct returned object | category exist")
    void checkExistAndGetCategoryByName_correctReturnedObject_categoryExists() {
        String name = "coffee";

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(category1));

        Category result = underTest.checkExistAndGetCategoryByName(name);

        verify(categoryRepository, times(1))
                .findByName(anyString());

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(1),
                () -> assertThat(result.getName()).isEqualTo("coffee")
        );
    }
}