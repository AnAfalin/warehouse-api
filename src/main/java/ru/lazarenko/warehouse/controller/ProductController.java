package ru.lazarenko.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.PriceRangeDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseDto addProduct(@RequestBody @Valid ProductDto request) {
        return productService.createProduct(request);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "category") String category) {
        if (!StringUtils.hasLength(category)) {
            return productService.getAllProducts();
        }
        return productService.getProductsByCategory(category);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/search-in-price")
    public List<ProductDto> getProductsInPriceRange(@Valid PriceRangeDto request) {
        return productService.getProductsByPriceRange(request);
    }

}
