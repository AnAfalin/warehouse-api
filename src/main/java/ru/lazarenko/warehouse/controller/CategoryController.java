package ru.lazarenko.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.ResponseDto;
import ru.lazarenko.warehouse.service.CategoryService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseDto addCategory(@RequestBody @Valid CategoryDto request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryDto> getAllRegions() {
        return categoryService.getAllCategories();
    }

}
