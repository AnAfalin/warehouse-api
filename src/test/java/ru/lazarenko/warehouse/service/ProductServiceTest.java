package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.PriceRangeDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.repository.ProductRepository;
import ru.lazarenko.warehouse.service.mapper.ProductMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    ProductService underTest;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    CategoryService categoryService;

    @MockBean
    ProductMapper productMapper;

    Category category;
    Product product1;
    Product product2;
    CategoryDto categoryDto;
    ProductDto productDto1;
    ProductDto productDto2;
    ProductDto productRequest;

    @BeforeEach
    void prepare() {
        category = Category.builder().id(1).name("coffee").build();

        product1 = Product.builder()
                .id(1)
                .name("latte")
                .price(new BigDecimal(180))
                .category(category)
                .build();
        product2 = Product.builder()
                .id(2)
                .name("cappuccino")
                .price(new BigDecimal(150))
                .category(category)
                .build();

        categoryDto = CategoryDto.builder().name("coffee").build();

        productDto1 = ProductDto.builder()
                .id(1)
                .name("latte")
                .price(new BigDecimal(180))
                .category(categoryDto)
                .build();

        productDto2 = ProductDto.builder()
                .id(2)
                .name("cappuccino")
                .price(new BigDecimal(150))
                .category(categoryDto)
                .build();

        productRequest = ProductDto.builder()
                .name("cappuccino")
                .price(new BigDecimal(150))
                .category(categoryDto)
                .build();
    }

    @Test
    @DisplayName("create product | NoFoundElementException | category does not exist")
    void createProduct_noFoundElementException_categoryDoesNotExist() {
        doThrow(NoFoundElementException.class)
                .when(categoryService)
                .checkExistAndGetCategoryByName(anyString());

        assertThrows(NoFoundElementException.class, () -> underTest.createProduct(productRequest));
    }

    @Test
    @DisplayName("create product | NoFoundElementException | category does not exist")
    void createProduct_successfullyCreated_categoryExist() {
        when(categoryService.checkExistAndGetCategoryByName(anyString()))
                .thenReturn(category);

        when(productMapper.toProduct(any(ProductDto.class)))
                .thenReturn(product2);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product2);

        ResponseDto result = underTest.createProduct(productRequest);

        verify(productRepository, times(1))
                .save(any(Product.class));
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getMessage()).isEqualTo("Product successful created: id='2'");
    }

    @Test
    @DisplayName("get all products | empty result list | products do not exist")
    void getAllProducts_resultListIsEmpty_productsDoNotExist() {
        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get all products | result list not empty | products exist")
    void getAllProducts_resultListIsNotEmpty_productsExist() {
        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        when(productMapper.toProductDtoList(anyList()))
                .thenReturn(List.of(productDto1, productDto2));

        List<ProductDto> result = underTest.getAllProducts();

        verify(productRepository, times(1))
                .findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("latte");
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getName()).isEqualTo("cappuccino");
    }

    @Test
    @DisplayName("check exist and get product by id | NoFoundElementException | product does not exist")
    void checkExistAndGetProductById_noFoundElementException_productDoesNotExist() {
        Integer id = 100;
        when(productRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.checkExistAndGetProductById(id));
    }

    @Test
    @DisplayName("check exist and get product by id | return product | product exist")
    void checkExistAndGetProductById_returnProduct_productExist() {
        Integer id = 1;
        when(productRepository.findById(anyInt()))
                .thenReturn(Optional.of(product1));

        Product result = underTest.checkExistAndGetProductById(id);

        verify(productRepository, times(1))
                .findById(anyInt());

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("latte");
    }

    @Test
    @DisplayName("get products by price range | empty result list | products do not exist")
    void getProductsByPriceRange_resultListIsEmpty_productsDoNotExist() {
        PriceRangeDto request = PriceRangeDto.builder()
                .min(0)
                .max(100)
                .build();

        when(productRepository.findProductsByMinAndMaxPrice(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getProductsByPriceRange(request);

        verify(productRepository, times(1))
                .findProductsByMinAndMaxPrice(any(BigDecimal.class), any(BigDecimal.class));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get products by price range  | result list not empty | products exist")
    void getProductsByPriceRange_resultListIsNotEmpty_productsExist() {
        PriceRangeDto request = PriceRangeDto.builder()
                .min(160)
                .max(200)
                .build();

        when(productRepository.findProductsByMinAndMaxPrice(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(product2));

        when(productMapper.toProductDtoList(anyList()))
                .thenReturn(List.of(productDto2));

        List<ProductDto> result = underTest.getProductsByPriceRange(request);

        verify(productRepository, times(1))
                .findProductsByMinAndMaxPrice(any(BigDecimal.class), any(BigDecimal.class));

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("cappuccino");
    }

    @Test
    @DisplayName("get products by category | NoFoundElementException| category do not exist")
    void getProductsByCategory_noFoundElementException_categoryDoNotExist() {
        String categoryName = "unknown";

        doThrow(NoFoundElementException.class)
                .when(categoryService)
                .checkExistAndGetCategoryByName(categoryName);

        assertThrows(NoFoundElementException.class, () -> underTest.getProductsByCategory(categoryName));
    }


    @Test
    @DisplayName("get products by category | empty result list | products do not exist and category exist")
    void getProductsByCategory_resultListIsEmpty_productsDoNotExist() {
        String categoryName = "tea";

        when(categoryService.checkExistAndGetCategoryByName(categoryName))
                .thenReturn(Category.builder().id(1).name("tea").build());

        when(productRepository.findAllByCategoryId(anyInt()))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getProductsByCategory(categoryName);

        verify(productRepository, times(1))
                .findAllByCategoryId(anyInt());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get products by category | result list not empty | products exist and category exist")
    void getProductsByCategory_resultListIsNotEmpty_productsExist() {
        String categoryName = "coffee";

        when(categoryService.checkExistAndGetCategoryByName(categoryName))
                .thenReturn(category);

        when(productRepository.findAllByCategoryId(anyInt()))
                .thenReturn(List.of(product1, product2));

        when(productMapper.toProductDtoList(anyList()))
                .thenReturn(List.of(productDto1, productDto2));

        List<ProductDto> result = underTest.getProductsByCategory(categoryName);

        verify(productRepository, times(1))
                .findAllByCategoryId(anyInt());

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("latte");
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getName()).isEqualTo("cappuccino");
        assertThat(result.get(1).getCategory().getName()).isEqualTo("coffee");
    }
}