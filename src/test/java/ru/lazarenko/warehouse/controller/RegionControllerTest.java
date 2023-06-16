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
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.service.RegionService;

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

@WebMvcTest(RegionController.class)
class RegionControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    RegionService regionService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class ValidationRegionTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate region | size of validation list is 1 | filed 'name' is null")
        void validateRegion_correctSizeValidationList_fieldNameIsNull() {
            RegionDto test = new RegionDto();

            List<ConstraintViolation<RegionDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Region name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate region | size of validation list is 1 | filed 'name' is empty")
        void validateRegion_correctSizeValidationList_fieldNameIsEmpty() {
            RegionDto test = RegionDto.builder()
                    .name("")
                    .build();

            List<ConstraintViolation<RegionDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Region name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate region | size of validation list is empty | object is correct")
        void validateRegion_correctSizeValidationList_regionCorrect() {
            RegionDto test = RegionDto.builder()
                    .name("region name")
                    .build();

            List<ConstraintViolation<RegionDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(0, validationSet.size());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("add region | status is ok | request is correct")
    void addRegion_statusOk_requestIsCorrect() throws Exception {
        RegionDto request = RegionDto.builder()
                .name("Moscow")
                .build();
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.toString())
                .message("Region has been added successfully")
                .build();

        when(regionService.createRegion(any(RegionDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/regions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("get all regions | status is ok and result list is empty | regions don't exist")
    void getAllRegions_statusOkAndEmptyResultList_regionsDontExist() throws Exception {
        when(regionService.getAllRegions())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/regions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get all regions | status is ok and result list is not empty | regions exist")
    void getAllRegions_statusOkAndEmptyResultList_regionsExist() throws Exception {
        RegionDto region1 = RegionDto.builder()
                .id(1)
                .name("Moscow")
                .build();
        RegionDto region2 = RegionDto.builder()
                .id(1)
                .name("Sochi")
                .build();

        when(regionService.getAllRegions())
                .thenReturn(List.of(region1, region2));

        mvc.perform(MockMvcRequestBuilders.get("/api/regions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("Moscow"))
                .andExpect(jsonPath("$.[1].id").exists())
                .andExpect(jsonPath("$.[1].name").value("Sochi"));
    }

}