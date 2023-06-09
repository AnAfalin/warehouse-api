package ru.lazarenko.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.warehouse.dto.PriceRangeDto;
import ru.lazarenko.warehouse.dto.ProductDto;
import ru.lazarenko.warehouse.dto.ResponseDto;
import ru.lazarenko.warehouse.service.ProductService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseDto addProduct(@RequestBody @Valid ProductDto request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "category") String category) {
        if (!StringUtils.hasLength(category)) {
            return productService.getAllProducts();
        }
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/search-in-price")
    public List<ProductDto> getProductsInPriceRange(@RequestBody @Valid PriceRangeDto request) {
        return productService.getProductsByPriceRange(request);
    }

}
