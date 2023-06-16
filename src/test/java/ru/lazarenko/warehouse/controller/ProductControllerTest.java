package ru.lazarenko.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.PriceRangeDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.service.ProductService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class ValidationProductTest {

        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate product | validation list is empty | object is correct")
        void validateProduct_validationListIsEmpty_objectIsCorrect() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name("coffee and tea")
                    .build();
            ProductDto test = ProductDto.builder()
                    .name("coffee")
                    .price(new BigDecimal(100))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertTrue(validationSet.isEmpty());
        }

        @Test
        @DisplayName("validate product | size of validation list is 3 | all field object are incorrect")
        void validateProduct_correctSizeValidationList_allFieldObjectAreIncorrect() {
            ProductDto test = new ProductDto();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(3, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Product name cannot be empty or null")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Category cannot be null")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Price cannot be null"))
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'name' is null")
        void validateProduct_correctSizeValidationList_fieldNameIsNull() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name("coffee and tea")
                    .build();
            ProductDto test = ProductDto.builder()
                    .price(new BigDecimal(100))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Product name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'name' is empty")
        void validateProduct_correctSizeValidationList_fieldNameIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name("coffee and tea")
                    .build();
            ProductDto test = ProductDto.builder()
                    .name("")
                    .price(new BigDecimal(100))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Product name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'price' is null")
        void validateProduct_correctSizeValidationList_fieldPriceIsNull() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name("coffee and tea")
                    .build();
            ProductDto test = ProductDto.builder()
                    .name("coffee")
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Price cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'category' is null")
        void validateProduct_correctSizeValidationList_fieldCategoryIsNull() {
            ProductDto test = ProductDto.builder()
                    .name("coffee and tea")
                    .price(new BigDecimal(100))
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'name' of category is null")
        void validateProduct_correctSizeValidationList_fieldCategoryNameIsNull() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .build();
            ProductDto test = ProductDto.builder()
                    .name("coffee and tea")
                    .price(new BigDecimal(100))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate product | size of validation list is 1 | field 'name' of category is empty")
        void validateProduct_correctSizeValidationList_fieldCategoryNameIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name("")
                    .build();
            ProductDto test = ProductDto.builder()
                    .name("coffee and tea")
                    .price(new BigDecimal(100))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<ProductDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }
    }

    @Test
    @WithMockUser
    @DisplayName("add product | status is ok | request is correct")
    void addProduct_statusOk_requestIsCorrect() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("coffee and tea")
                .build();
        ProductDto request = ProductDto.builder()
                .name("coffee")
                .price(new BigDecimal(100))
                .category(categoryDto)
                .build();

        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Product successful created: id='1'")
                .build();

        when(productService.createProduct(any(ProductDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(response.getMessage()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @WithMockUser
    @DisplayName("add product | status is ok | category do not exist")
    void addProduct_statusNotFound_categoryDoNotExist() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("coffee and tea")
                .build();
        ProductDto request = ProductDto.builder()
                .name("coffee")
                .price(new BigDecimal(100))
                .category(categoryDto)
                .build();

        doThrow(NoFoundElementException.class)
                .when(productService)
                .createProduct(any(ProductDto.class));

        mvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("get products in price range | status is ok and empty list result| products not founded")
    void getProductsInPriceRange_statusOkAndEmptyListResult_productsNotFound() throws Exception {
        PriceRangeDto request = PriceRangeDto.builder()
                .min(10)
                .max(150)
                .build();

        when(productService.getProductsByPriceRange(any(PriceRangeDto.class)))
                .thenReturn(List.of());

        mvc.perform(get("/api/products/search-in-price")
                        .param("min", request.getMin().toString())
                        .param("max", request.getMax().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("get products in price range | status is ok | products founded")
    void getProductsInPriceRange_statusOkAndNotEmptyListResult_productsFound() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("coffee and tea")
                .build();
        ProductDto productDto = ProductDto.builder()
                .name("coffee")
                .price(new BigDecimal(100))
                .category(categoryDto)
                .build();
        PriceRangeDto request = PriceRangeDto.builder()
                .min(10)
                .max(150)
                .build();

        when(productService.getProductsByPriceRange(any(PriceRangeDto.class)))
                .thenReturn(List.of(productDto));

        mvc.perform(get("/api/products/search-in-price")
                        .param("min", request.getMin().toString())
                        .param("max", request.getMax().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("get all products without category | status is ok and empty list result| products not founded")
    void getAllProductsWithoutCategory_statusOkAndEmptyListResult_productsNotFound() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of());

        mvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("get all products without category | status is ok | products founded")
    void getAllProductsWithoutCategory_statusOkAndNotEmptyListResult_productsFound() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("coffee and tea")
                .build();
        ProductDto productDto = ProductDto.builder()
                .id(1)
                .name("coffee")
                .price(new BigDecimal(100))
                .category(categoryDto)
                .build();

        when(productService.getAllProducts())
                .thenReturn(List.of(productDto));

        mvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].name").value("coffee"));
    }

    @Test
    @WithMockUser
    @DisplayName("get all products with category | status is ok and empty list result| products not founded")
    void getAllProductsWithCategory_statusOkAndEmptyListResult_productsNotFound() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of());

        mvc.perform(get("/api/products")
                        .param("category", "any category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("get all products with category | status is ok | products founded")
    void getAllProductsWithCategory_statusOkAndNotEmptyListResult_productsFound() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("coffee and tea")
                .build();
        ProductDto productDto = ProductDto.builder()
                .id(1)
                .name("coffee")
                .price(new BigDecimal(100))
                .category(categoryDto)
                .build();

        when(productService.getProductsByCategory(anyString()))
                .thenReturn(List.of(productDto));

        mvc.perform(get("/api/products")
                        .param("category", categoryDto.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("coffee"));
    }

    @Test
    @WithMockUser
    @DisplayName("get all products with category | status is ok | category do not exist")
    void getAllProductsWithCategory_statusMotFound_categoryDoNotExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(productService)
                .getProductsByCategory(anyString());

        mvc.perform(get("/api/products")
                        .param("category", "unknown category"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}