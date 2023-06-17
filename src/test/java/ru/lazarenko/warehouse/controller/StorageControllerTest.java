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
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.ChangeItemStorageRequest;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentRequest;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentResponse;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.service.ItemStorageService;
import ru.lazarenko.warehouse.service.StorageService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StorageController.class)
class StorageControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    StorageService storageService;

    @MockBean
    ItemStorageService itemStorageService;

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
        @DisplayName("validate storage | size of validation list is 1 | filed 'name' is null")
        void validateStorage_correctSizeValidationList_fieldNameIsNull() {
            RegionDto region = RegionDto.builder()
                    .name("Moscow")
                    .build();
            StorageDto test = StorageDto.builder()
                    .region(region)
                    .build();

            List<ConstraintViolation<StorageDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Storage name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate storage | size of validation list is 1 | filed 'name' is empty")
        void validateStorage_correctSizeValidationList_fieldNameIsEmpty() {
            RegionDto region = RegionDto.builder()
                    .name("Moscow")
                    .build();
            StorageDto test = StorageDto.builder()
                    .name("")
                    .region(region)
                    .build();

            List<ConstraintViolation<StorageDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Storage name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate storage | size of validation list is 1 | filed 'region' is null")
        void validateStorage_correctSizeValidationList_fieldRegionIsNull() {
            StorageDto test = StorageDto.builder()
                    .name("msk storage")
                    .build();

            List<ConstraintViolation<StorageDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Region cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate storage | size of validation list is 1 | filed 'region' is null")
        void validateStorage_correctSizeValidationList_fieldRegionNameIsNull() {
            RegionDto region = RegionDto.builder()
                    .build();
            StorageDto test = StorageDto.builder()
                    .name("msk storage")
                    .region(region)
                    .build();

            List<ConstraintViolation> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Region name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate storage | size of validation list is 1 | filed 'region' is empty")
        void validateStorage_correctSizeValidationList_fieldRegionNameIsEmpty() {
            RegionDto region = RegionDto.builder()
                    .name("")
                    .build();
            StorageDto test = StorageDto.builder()
                    .name("msk storage")
                    .region(region)
                    .build();

            List<ConstraintViolation> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Region name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }


        @Test
        @DisplayName("validate storage | size of validation list is empty | object is correct")
        void validateStorage_correctSizeValidationList_storageCorrect() {
            RegionDto region = RegionDto.builder()
                    .name("Moscow")
                    .build();
            StorageDto test = StorageDto.builder()
                    .name("msk storage")
                    .region(region)
                    .build();

            List<ConstraintViolation<StorageDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(0, validationSet.size());
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is empty | object is correct")
        void validateChangeItemStorageRequest_correctSizeValidationList_storageCorrect() {
            ChangeItemStorageRequest test = ChangeItemStorageRequest.builder()
                    .productId(1)
                    .storageId(2)
                    .count(5)
                    .build();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(0, validationSet.size());
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is 1 | filed 'productId' is empty")
        void validateChangeItemStorageRequest_correctSizeValidationList_fieldProductIdIsNull() {
            ChangeItemStorageRequest test = ChangeItemStorageRequest.builder()
                    .storageId(2)
                    .count(5)
                    .build();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Product id cannot null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is 1 | filed 'storageId' is empty")
        void validateChangeItemStorageRequest_correctSizeValidationList_fieldStorageIdIsNull() {
            ChangeItemStorageRequest test = ChangeItemStorageRequest.builder()
                    .productId(1)
                    .count(5)
                    .build();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Storage id cannot null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is 1 | filed 'count' is empty")
        void validateChangeItemStorageRequest_correctSizeValidationList_fieldCountIsNull() {
            ChangeItemStorageRequest test = ChangeItemStorageRequest.builder()
                    .productId(1)
                    .storageId(2)
                    .build();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Count cannot null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is 1 | filed 'count' is less 0")
        void validateChangeItemStorageRequest_correctSizeValidationList_fieldCountIsLess1() {
            ChangeItemStorageRequest test = ChangeItemStorageRequest.builder()
                    .productId(1)
                    .storageId(2)
                    .count(-1)
                    .build();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(1, validationSet.size());
        }

        @Test
        @DisplayName("validate ChangeItemStorageRequest | size of validation list is 1 | filed 'count' is less 0")
        void validateChangeItemStorageRequest_correctSizeValidationList_allFieldsAreIncorrect() {
            ChangeItemStorageRequest test = new ChangeItemStorageRequest();

            List<ConstraintViolation<ChangeItemStorageRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(3, validationSet.size());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("add storage | status is ok | request is correct")
    void addStorage_statusOk_requestIsCorrect() throws Exception {
        RegionDto region = RegionDto.builder()
                .name("Moscow")
                .build();
        StorageDto request = StorageDto.builder()
                .name("Moscow storage")
                .region(region)
                .build();

        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.toString())
                .message("Storage has been added successfully")
                .build();

        when(storageService.createStorage(any(StorageDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/storages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Storage has been added successfully"));
    }

    @Test
    @WithMockUser
    @DisplayName("add storage | status is 'not found' | request is correct")
    void addStorage_statusNotFound_regionDoNotExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(storageService)
                .createStorage(any(StorageDto.class));

        mvc.perform(post("/api/storages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new StorageDto())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("get all storages | status is ok and result list is empty | storages don't exist")
    void getAllRegions_statusOkAndEmptyResultList_regionsDoNotExist() throws Exception {
        when(storageService.getAllStorages())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/storages"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get all storages | status is ok and result list is not empty | storages exist")
    void getAllStorages_statusOkAndEmptyResultList_storagesExist() throws Exception {
        RegionDto region = RegionDto.builder()
                .id(1)
                .name("Moscow")
                .build();
        StorageDto storage = StorageDto.builder()
                .id(2)
                .name("Moscow storage")
                .region(region)
                .build();

        when(storageService.getAllStorages())
                .thenReturn(List.of(storage));

        mvc.perform(MockMvcRequestBuilders.get("/api/storages"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].name").value("Moscow storage"))
                .andExpect(jsonPath("$.[0].region").exists())
                .andExpect(jsonPath("$.[0].region.name").value("Moscow"));
    }

    @Test
    @WithMockUser
    @DisplayName("add product to storage | status is ok and successful response | storage and product exist")
    void addProductToStorage_statusOkAndEmptyResultList_storageAndProductExist() throws Exception {
        RegionDto region = RegionDto.builder()
                .id(1)
                .name("Moscow")
                .build();
        StorageDto storage = StorageDto.builder()
                .id(2)
                .name("Moscow storage")
                .region(region)
                .build();
        Product product = Product.builder()
                .id(5)
                .name("herbal tea")
                .price(new BigDecimal(100))
                .build();

        ChangeItemStorageRequest request = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(2)
                .count(5)
                .build();

        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Total count of product with id='%s' on storage with id='%s': %s"
                        .formatted(product.getId(), storage.getId(), request.getCount()))
                .build();

        when(storageService.increaseProductInStorage(any(ChangeItemStorageRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/api/storages/add-product")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Total count of product with id='5' on storage with id='2': 5"));
    }

    @Test
    @WithMockUser
    @DisplayName("add product to storage | status is not found | storage or product do not exist")
    void addProductToStorage_statusNotFound_storageOrProductsDoNotExist() throws Exception {
        ChangeItemStorageRequest request = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(2)
                .count(5)
                .build();

        doThrow(NoFoundElementException.class)
                .when(storageService)
                .increaseProductInStorage(any(ChangeItemStorageRequest.class));

        mvc.perform(post("/api/storages/add-product")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("decrease product to storage | status is ok and successful response | storage and product exist")
    void decreaseProductToStorage_statusOkAndEmptyResultList_storageAndProductExist() throws Exception {
        RegionDto region = RegionDto.builder()
                .id(1)
                .name("Moscow")
                .build();
        StorageDto storage = StorageDto.builder()
                .id(2)
                .name("Moscow storage")
                .region(region)
                .build();
        Product product = Product.builder()
                .id(5)
                .name("herbal tea")
                .price(new BigDecimal(100))
                .build();

        ChangeItemStorageRequest request = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(2)
                .count(5)
                .build();

        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Total count of product with id='%s' on storage with id='%s': %s"
                        .formatted(product.getId(), storage.getId(), 8 - request.getCount()))
                .build();

        when(storageService.decreaseProductInStorage(any(ChangeItemStorageRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/api/storages/decrease-product")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Total count of product with id='5' on storage with id='2': 3"));
    }

    @Test
    @WithMockUser
    @DisplayName("decrease product to storage | status is not found | storage or product do not exist")
    void decreaseProductToStorage_statusNotFound_storageOrProductsDoNotExist() throws Exception {
        ChangeItemStorageRequest request = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(2)
                .count(5)
                .build();

        doThrow(NoFoundElementException.class)
                .when(storageService)
                .decreaseProductInStorage(any(ChangeItemStorageRequest.class));

        mvc.perform(post("/api/storages/decrease-product")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName(""" 
            get all products by storage
            | status is ok and not empty result list
            | request param 'category' is not null and products exist
            """)
    void getAllProductsByStorage_statusOkAndNotEmptyResultList_storageAndProductExist() throws Exception {
        ProductDto product1 = ProductDto.builder()
                .id(1)
                .name("herbal tea")
                .price(new BigDecimal(100))
                .build();
        ProductDto product2 = ProductDto.builder()
                .id(2)
                .name("green tea")
                .price(new BigDecimal(120))
                .build();

        List<ProductDto> result = List.of(product1, product2);

        when(storageService.getAllProductsByStorageId(anyInt()))
                .thenReturn(result);

        mvc.perform(get("/api/storages/1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].name").value("herbal tea"))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[1].name").value("green tea"));
    }

    @Test
    @WithMockUser
    @DisplayName(""" 
            get all products by storage
            | status is ok and empty result list
            | request param 'category' is not null and products exist
            """)
    void getAllProductsByStorage_statusOkAndEmptyResultList_storageAndProductExist() throws Exception {
        when(storageService.getAllProductsByStorageId(anyInt()))
                .thenReturn(List.of());

        mvc.perform(get("/api/storages/1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0))
                .andExpect(jsonPath("$.[0].id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName(""" 
            get all products by storage
            | status is ok and not empty result list
            | request param 'category' is not null and products exist
            """)
    void getAllProductsByStorageAndCategory_statusOkAndNotEmptyResultList_storageAndProductExist() throws Exception {
        ProductDto product1 = ProductDto.builder()
                .id(1)
                .name("herbal tea")
                .price(new BigDecimal(100))
                .build();
        ProductDto product2 = ProductDto.builder()
                .id(2)
                .name("green tea")
                .price(new BigDecimal(120))
                .build();
        String category = "tea";

        List<ProductDto> result = List.of(product1, product2);

        when(storageService.getAllProductsByStorageIdAndCategory(anyInt(), anyString()))
                .thenReturn(result);

        mvc.perform(get("/api/storages/1/products")
                        .param("category", category))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].name").value("herbal tea"))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[1].name").value("green tea"));
    }

    @Test
    @WithMockUser
    @DisplayName(""" 
            get all products by storage
            | status is ok and empty result list
            | request param 'category' is not null and products exist
            """)
    void getAllProductsByStorageAndCategory_statusOkAndEmptyResultList_storageAndProductExist() throws Exception {
        when(storageService.getAllProductsByStorageIdAndCategory(anyInt(), anyString()))
                .thenReturn(List.of());

        String category = "tea";

        mvc.perform(get("/api/storages/1/products")
                        .param("category", category))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0))
                .andExpect(jsonPath("$.[0].id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get storage for loading or shipment | status is ok and not empty result list | storage and product exist")
    void getStorageForLoadingShipment_statusOkAndNotEmptyResultList_storageAndProductsExist() throws Exception {
        LoadingShipmentRequest request = LoadingShipmentRequest.builder()
                .productId(1)
                .region("Moscow")
                .count(5)
                .type(OperationType.LOADING)
                .build();
        StorageDto storage = StorageDto.builder()
                .id(2)
                .name("Moscow storage")
                .region(new RegionDto(1, "Moscow"))
                .build();
        LoadingShipmentResponse response = LoadingShipmentResponse.builder()
                .storages(List.of(storage))
                .build();

        when(itemStorageService.findStorageForLoadingOrShipment(any(LoadingShipmentRequest.class)))
                .thenReturn(response);

        mvc.perform(get("/api/storages/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("productId", request.getProductId().toString())
                        .param("region", request.getRegion())
                        .param("count", request.getCount().toString())
                        .param("type", request.getType().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storages").exists())
                .andExpect(jsonPath("$.storages").isNotEmpty())
                .andExpect(jsonPath("$.storages.size()").value(1))
                .andExpect(jsonPath("$.storages[0].name").value("Moscow storage"));
    }

    @Test
    @WithMockUser
    @DisplayName("get storage for loading or shipment | status is ok and empty result list | storage and product exist")
    void getStorageForLoadingShipment_statusOkAndEmptyResultList_storageAndProductsExist() throws Exception {
        LoadingShipmentResponse response = LoadingShipmentResponse.builder()
                .storages(List.of())
                .build();

        when(itemStorageService.findStorageForLoadingOrShipment(any(LoadingShipmentRequest.class)))
                .thenReturn(response);

        mvc.perform(get("/api/storages/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("productId", "1")
                        .param("region", "Moscow")
                        .param("count", "100")
                        .param("type", "SHIPMENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storages").exists())
                .andExpect(jsonPath("$.storages").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("get storage for loading or shipment | status is ok and empty result list | storage and product exist")
    void getStorageForLoadingShipment_statusNotFound_storageOrProductsDoNotExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(itemStorageService)
                .findStorageForLoadingOrShipment(any(LoadingShipmentRequest.class));

        mvc.perform(get("/api/storages/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("productId", "1")
                        .param("region", "Moscow")
                        .param("count", "100")
                        .param("type", "SHIPMENT"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.storages").doesNotExist());
    }

}