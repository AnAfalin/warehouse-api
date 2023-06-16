package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.product.PriceRangeDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.repository.ProductRepository;
import ru.lazarenko.warehouse.service.mapper.ProductMapper;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Transactional
    public ResponseDto createProduct(ProductDto request) {
        Category category = categoryService.checkExistAndGetCategoryByName(request.getCategory().getName());

        Product product = productMapper.toProduct(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product successful created: {}", savedProduct);

        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Product successful created: id='%s'".formatted(savedProduct.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toProductDtoList(products);
    }

    @Transactional(readOnly = true)
    public Product checkExistAndGetProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Product with id='%s' not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String category) {
        Category foundCategory = categoryService.checkExistAndGetCategoryByName(category);

        List<Product> products = productRepository.findAllByCategoryId(foundCategory.getId());
        return productMapper.toProductDtoList(products);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByPriceRange(PriceRangeDto request) {
        List<Product> products = productRepository
                .findProductsByMinAndMaxPrice(new BigDecimal(request.getMin()), new BigDecimal(request.getMax()));
        return productMapper.toProductDtoList(products);
    }
}
