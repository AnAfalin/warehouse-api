package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.repository.CategoryRepository;
import ru.lazarenko.warehouse.service.mapper.CategoryMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public ResponseDto createCategory(CategoryDto request) {
        checkUniqueName(request.getName());

        Category category = categoryMapper.toCategory(request);
        Category savedCategory = categoryRepository.save(category);

        log.error("Category successful created: {}", savedCategory);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Category successful created: id='%s'".formatted(savedCategory.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public void checkUniqueName(String name) {
        Optional<Category> foundWarehouse = categoryRepository.findByName(name);
        if (foundWarehouse.isPresent()) {
            log.error("Category with name='{}' already exist", name);
            throw new NoUniqueObjectException("Category with name='%s' already exist".formatted(name));
        }
    }

    @Transactional(readOnly = true)
    public Category checkExistAndGetCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NoFoundElementException("Category with name='%s' not found".formatted(name)));
    }

}
