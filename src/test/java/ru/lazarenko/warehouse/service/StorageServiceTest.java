package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.ChangeItemStorageRequest;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.*;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.exception.ProductCountException;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.repository.StorageRepository;
import ru.lazarenko.warehouse.service.mapper.StorageMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class StorageServiceTest {
    @Autowired
    StorageService underTest;

    @MockBean
    StorageRepository storageRepository;

    @MockBean
    RegionService regionService;

    @MockBean
    ProductService productService;

    @MockBean
    ItemStorageService itemStorageService;

    @MockBean
    CategoryService categoryService;

    @MockBean
    StorageMapper storageMapper;

    @MockBean
    OperationHistoryService operationHistoryService;

    Category category;
    Product product;
    Region region;
    Storage storage;
    OperationHistory operation;

    StorageDto storageDto;
    RegionDto regionDto;

    StorageDto request;
    ProductDto productDto;

    ItemStorage itemStorage;

    @BeforeEach
    void prepare() {
        category = Category.builder().id(1).name("coffee").build();

        product = Product.builder()
                .id(1)
                .name("cappuccino")
                .price(new BigDecimal(150))
                .category(category)
                .build();

        region = Region.builder().id(1).name("Sochi").build();

        storage = Storage.builder().id(1).name("Sochi-str-1").region(region).build();

        operation = OperationHistory.builder()
                .operation(OperationType.LOADING)
                .product(product)
                .storage(storage)
                .date(LocalDateTime.of(2023, 5, 10, 13, 0, 0))
                .count(25)
                .build();

        storageDto = StorageDto.builder().id(1).name("Sochi-str-1").build();
        regionDto = RegionDto.builder().id(1).name("Sochi").build();

        productDto = ProductDto.builder()
                .id(1)
                .name("cappuccino")
                .price(new BigDecimal(150))
                .build();

        request = StorageDto.builder().name("Sochi-str-1").region(RegionDto.builder().name("Sochi").build()).build();

        itemStorage = ItemStorage.builder()
                .id(11)
                .product(product)
                .storage(storage)
                .count(150)
                .build();
    }

    @Test
    @DisplayName("""
            create storage
            | noUniqueObjectException
            | name is not unique
            """)
    void createStorage_noUniqueObjectException_nameIsNotUnique() {
        when(storageMapper.toStorage(any(StorageDto.class)))
                .thenReturn(storage);

        when(storageRepository.findByName(anyString()))
                .thenReturn(Optional.of(new Storage()));

        assertThrows(NoUniqueObjectException.class, () -> underTest.createStorage(request));
    }

    @Test
    @DisplayName("""
            create storage
            | noFoundElementException
            | name is unique, but region does not exist
            """)
    void createStorage_noFoundElementException_reginDoesNotExist() {
        when(storageMapper.toStorage(any(StorageDto.class)))
                .thenReturn(storage);

        when(storageRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        doThrow(NoFoundElementException.class)
                .when(regionService)
                .checkExistAndGetRegionByName(anyString());

        assertThrows(NoFoundElementException.class, () -> underTest.createStorage(request));
    }

    @Test
    @DisplayName("""
            create storage
            | successful create
            | name is unique and region exist
            """)
    void createStorage_successfulCreate_regionExistAndNameIsUnique() {
        when(storageMapper.toStorage(any(StorageDto.class)))
                .thenReturn(storage);

        when(storageRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(regionService.checkExistAndGetRegionByName(anyString()))
                .thenReturn(region);

        when(storageRepository.save(any(Storage.class)))
                .thenReturn(storage);

        ResponseDto result = underTest.createStorage(request);

        verify(storageRepository, times(1))
                .save(any(Storage.class));

        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getMessage()).isEqualTo("Storage successful created: id='1'");
    }

    @Test
    @DisplayName("""
            check exist and get storage by id
            | noFoundElementException
            | category does not exist
            """)
    void checkExistAndGetStorageById_noFoundElementException_categoryDoesNotExist() {
        Integer id = 100;

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.checkExistAndGetStorageById(id));
    }

    @Test
    @DisplayName("""
            check exist and get storage by id
            | correct returned object
            | category exist
            """)
    void checkExistAndGetStorageById_correctReturnedObject_categoryExists() {
        Integer id = 1;

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        Storage result = underTest.checkExistAndGetStorageById(id);

        verify(storageRepository, times(1))
                .findById(anyInt());

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(1),
                () -> assertThat(result.getName()).isEqualTo("Sochi-str-1")
        );
    }

    @Test
    @DisplayName("""
            check unique name
            | noUniqueObjectException
            | storage name is not unique
            """)
    void checkUniqueName_noUniqueObjectException_storageNameIsNotUnique() {
        String name = "Sochi-str-1";

        when(storageRepository.findByName(anyString()))
                .thenReturn(Optional.of(storage));

        assertThrows(NoUniqueObjectException.class, () -> underTest.checkUniqueName(name));
    }

    @Test
    @DisplayName("""
            check unique name
            | no exception
            | storage name is unique
            """)
    void checkUniqueName_noException_storageNameIsUnique() {
        String name = "new-storage";

        when(storageRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        underTest.checkUniqueName(name);

        verify(storageRepository, times(1))
                .findByName(anyString());
    }

    @Test
    @DisplayName("""
            get all products by storageId
            | noFoundElementException
            | storage does not exist
            """)
    void getAllProductsByStorageId_noFoundElementException_storageDoesNotExist() {
        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.getAllProductsByStorageId(any()));
        verify(itemStorageService, times(0))
                .getProductsByStorageId(anyInt());
    }

    @Test
    @DisplayName("""
            get all products by storageId
            | empty result list
            | storage exist, but products do not exist
            """)
    void getAllProductsByStorageId_emptyResultList_storageExistAndProductsDoNotExist() {
        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(itemStorageService.getProductsByStorageId(anyInt()))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getAllProductsByStorageId(1);

        verify(itemStorageService, times(1))
                .getProductsByStorageId(anyInt());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            get all products by storageId
            | not empty result list
            | storage and products exist
            """)
    void getAllProductsByStorageId_resultListNotEmpty_storageExistAndProductsExist() {
        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(itemStorageService.getProductsByStorageId(anyInt()))
                .thenReturn(List.of(productDto));

        List<ProductDto> result = underTest.getAllProductsByStorageId(1);

        verify(itemStorageService, times(1))
                .getProductsByStorageId(anyInt());

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("cappuccino");
    }

    @Test
    @DisplayName("""
            get all products by storageId and category
            | noFoundElementException
            | storage does not exist
            """)
    void getAllProductsByStorageIdAndCategory_noFoundElementException_storageDoesNotExist() {
        Integer storageId = 100;
        String categoryName = "coffee";

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class,
                () -> underTest.getAllProductsByStorageIdAndCategory(storageId, categoryName));
        verify(itemStorageService, times(0))
                .getProductsByStorageId(anyInt());
    }

    @Test
    @DisplayName("""
            get all products by storageId and category
            | noFoundElementException
            | storage exist, but category does not exist
            """)
    void getAllProductsByStorageIdAndCategory_noFoundElementException_categoryDoesNotExist() {
        Integer storageId = 1;
        String categoryName = "unknown";

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        doThrow(NoFoundElementException.class)
                .when(categoryService)
                .checkExistAndGetCategoryByName(anyString());

        assertThrows(NoFoundElementException.class,
                () -> underTest.getAllProductsByStorageIdAndCategory(storageId, categoryName));
        verify(itemStorageService, times(0))
                .getProductsByStorageId(anyInt());
    }

    @Test
    @DisplayName("""
            get all products by storageId and category
            | empty result list
            | storage and category exist, but products do not exist
            """)
    void getAllProductsByStorageIdAndCategory_emptyResultList_storageAndCategoryExistAndProductsDoNotExist() {
        Integer storageId = 1;
        String categoryName = "coffee";

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(categoryService.checkExistAndGetCategoryByName(anyString()))
                .thenReturn(category);

        when(itemStorageService.getProductsByStorageId(anyInt()))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getAllProductsByStorageIdAndCategory(storageId, categoryName);

        verify(itemStorageService, times(1))
                .getProductsByStorageIdAndCategory(anyInt(), anyString());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            get all products by storageId and category
            | not empty result list
            | storage and category exist, and products exist
            """)
    void getAllProductsByStorageIdAndCategory_resultListNotEmpty_storageAndCategoryExistAndProductsExist() {
        Integer storageId = 1;
        String categoryName = "coffee";

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(categoryService.checkExistAndGetCategoryByName(anyString()))
                .thenReturn(category);

        when(itemStorageService.getProductsByStorageIdAndCategory(anyInt(), anyString()))
                .thenReturn(List.of(productDto));


        List<ProductDto> result = underTest.getAllProductsByStorageIdAndCategory(storageId, categoryName);

        verify(itemStorageService, times(1))
                .getProductsByStorageIdAndCategory(anyInt(), anyString());

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("cappuccino");
    }

    @Test
    void getAllStorages_emptyResultList_storagesDoNotExist() {
        when(storageRepository.findAll())
                .thenReturn(List.of());

        List<StorageDto> result = underTest.getAllStorages();

        verify(storageRepository, times(1))
                .findAll();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            get all storages
            | not empty result list
            | storages exist
            """)
    void getAllStorages_resultListIsNotEmpty_storagesExist() {
        when(storageRepository.findAll())
                .thenReturn(List.of(storage));

        when(storageMapper.toStorageDtoList(anyList()))
                .thenReturn(List.of(storageDto));

        List<StorageDto> result = underTest.getAllStorages();

        verify(storageRepository, times(1))
                .findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Sochi-str-1");
    }

    @Test
    @DisplayName("""
            increase product in storage
            | noFoundElementException
            | storage does not exist
            """)
    void increaseProductInStorage_noFoundElementException_storageDoesNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class,
                () -> underTest.increaseProductInStorage(changeItemRequest));

        verify(itemStorageService, times(0))
                .createItem(any(ItemStorage.class));
    }

    @Test
    @DisplayName("""
            increase product in storage
            | noFoundElementException
            | storage exist, but product does not exist
            """)
    void increaseProductInStorage_noFoundElementException_storageExistAndProductDoesNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        doThrow(NoFoundElementException.class)
                .when(productService)
                .checkExistAndGetProductById(anyInt());

        assertThrows(NoFoundElementException.class,
                () -> underTest.increaseProductInStorage(changeItemRequest));

        verify(itemStorageService, times(0))
                .createItem(any(ItemStorage.class));
    }

    @Test
    @DisplayName("""
            increase product in storage
            | successfully increase
            | storage and product exist, but item does not exist
            """)
    void increaseProductInStorage_successfullyIncrease_storageAndProductExistItemDoesNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(itemStorageService.getItemByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        when(itemStorageService.createItem(any(ItemStorage.class)))
                .thenReturn(ResponseDto.builder()
                        .message("Total count of product with id='%s' on storage with id='%s': %s"
                                .formatted(product.getId(), storage.getId(), changeItemRequest.getCount()))
                        .build());

        ResponseDto result = underTest.increaseProductInStorage(changeItemRequest);

        verify(itemStorageService, times(1))
                .createItem(any(ItemStorage.class));

        assertThat(result.getMessage())
                .isEqualTo("Total count of product with id='1' on storage with id='1': 10");
    }

    @Test
    @DisplayName("""
            increase product in storage
            | successfully increase
            | storage and product exist, and item exist
            """)
    void increaseProductInStorage_successfullyIncrease_storageAndProductExistItemExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(itemStorageService.getItemByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.of(itemStorage));

        when(itemStorageService.createItem(any(ItemStorage.class)))
                .thenReturn(ResponseDto.builder()
                        .message("Total count of product with id='%s' on storage with id='%s': %s"
                                .formatted(product.getId(), storage.getId(),
                                        itemStorage.getCount() + changeItemRequest.getCount()))
                        .build());

        ResponseDto result = underTest.increaseProductInStorage(changeItemRequest);

        verify(itemStorageService, times(1))
                .createItem(any(ItemStorage.class));

        assertThat(result.getMessage())
                .isEqualTo("Total count of product with id='1' on storage with id='1': 160");
    }

    @Test
    @DisplayName("""
            decrease product in storage
            | noFoundElementException
            | storage does not exist
            """)
    void decreaseProductInStorage_noFoundElementException_storageDoesNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class,
                () -> underTest.decreaseProductInStorage(changeItemRequest));

        verify(itemStorageService, times(0))
                .updateItemAndGetSaved(any(ItemStorage.class));
    }

    @Test
    @DisplayName("""
            decrease product in storage
            | noFoundElementException
            | storage exist, but product does not exist
            """)
    void decreaseProductInStorage_noFoundElementException_storageExistAndProductDoesNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        doThrow(NoFoundElementException.class)
                .when(productService)
                .checkExistAndGetProductById(anyInt());

        assertThrows(NoFoundElementException.class,
                () -> underTest.decreaseProductInStorage(changeItemRequest));

        verify(itemStorageService, times(0))
                .updateItemAndGetSaved(any(ItemStorage.class));
    }

    @Test
    @DisplayName("""
            decrease product in storage
            | noFoundElementException
            | storage and product exist, but item does not exist
            """)
    void decreaseProductInStorage_noFoundElementException_storageAndProductExistItemDoNotExist() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(itemStorageService.getItemByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class,
                () -> underTest.decreaseProductInStorage(changeItemRequest));

        verify(itemStorageService, times(0))
                .updateItemAndGetSaved(any(ItemStorage.class));
    }

    @Test
    @DisplayName("""
            decrease product in storage
            | successfully decrease
            | storage, product, item exist, and count in storage is enough
            """)
    void decreaseProductInStorage_successfullyDecrease_storageAndProductExistItemExistAndCountIsEnough() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(10)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(itemStorageService.getItemByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.of(itemStorage));

        when(itemStorageService.updateItemAndGetSaved(any(ItemStorage.class)))
                .thenReturn(itemStorage);

        ResponseDto result = underTest.decreaseProductInStorage(changeItemRequest);

        verify(itemStorageService, times(1))
                .updateItemAndGetSaved(any(ItemStorage.class));

        assertThat(result.getMessage())
                .isEqualTo("Total count of product with id='1' on storage with id='1': 140");
    }

    @Test
    @DisplayName("""
            decrease product in storage
            | productCountException
            | storage, product, item exist, but count in storage is not enough
            """)
    void decreaseProductInStorage_productCountException_storageAndProductExistItemExistAndCountIsNotEnough() {
        ChangeItemStorageRequest changeItemRequest = ChangeItemStorageRequest.builder()
                .productId(1)
                .storageId(1)
                .count(200)
                .build();

        when(storageRepository.findById(anyInt()))
                .thenReturn(Optional.of(storage));

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(itemStorageService.getItemByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.of(itemStorage));

        assertThrows(ProductCountException.class,
                () -> underTest.decreaseProductInStorage(changeItemRequest));
    }
}