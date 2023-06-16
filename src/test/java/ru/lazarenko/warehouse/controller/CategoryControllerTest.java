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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.service.CategoryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class ValidationCategoryTest {

        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate category | size of validation list is 1 | filed 'name' is null")
        void validateCategory_correctSizeValidationList_fieldNameIsNull() {
            CategoryDto test = new CategoryDto();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate category | size of validation list is 1 | filed 'name' is empty")
        void validateCategory_correctSizeValidationList_fieldNameIsEmpty() {
            CategoryDto test = CategoryDto.builder()
                    .name("")
                    .build();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate category | size of validation list is empty | object is correct")
        void validateCategory_correctSizeValidationList_regionCorrect() {
            CategoryDto test = CategoryDto.builder()
                    .name("category name")
                    .build();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(0, validationSet.size());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("add category | status is ok | request is correct")
    void addCategory_statusOk_requestIsCorrect() throws Exception {
        CategoryDto category = CategoryDto.builder()
                .id(1)
                .name("tea")
                .build();
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.toString())
                .message("Category has been added successfully")
                .build();

        when(categoryService.createCategory(any(CategoryDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(category)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("get all categories | status is ok and result list is empty | categories don't exist")
    void getAllCategories_statusOkAndEmptyResultList_regionsDontExist() throws Exception {
        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get all categories | status is ok and result list is not empty | categories exist")
    void getAllCategories_statusOkAndEmptyResultList_requestExist() throws Exception {
        CategoryDto category1 = CategoryDto.builder()
                .id(1)
                .name("tea")
                .build();
        CategoryDto category2 = CategoryDto.builder()
                .id(1)
                .name("coffee")
                .build();

        when(categoryService.getAllCategories())
                .thenReturn(List.of(category1, category2));

        mvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("tea"))
                .andExpect(jsonPath("$.[1].id").exists())
                .andExpect(jsonPath("$.[1].name").value("coffee"));
    }
}