package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentRequest;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.*;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.repository.ItemStorageRepository;
import ru.lazarenko.warehouse.service.mapper.ProductMapper;
import ru.lazarenko.warehouse.service.mapper.StorageMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemStorageServiceTest {
    @Autowired
    ItemStorageService underTest;

    @MockBean
    ItemStorageRepository itemStorageRepository;

    @MockBean
    ProductService productService;

    @MockBean
    RegionService regionService;

    @MockBean
    StorageMapper storageMapper;

    @MockBean
    ProductMapper productMapper;

    Region region;
    Storage storage;
    Category category;
    Product product;
    ProductDto productDto;
    ItemStorage itemStorage;

    @BeforeEach
    void prepare() {
        region = Region.builder().id(1).name("Sochi").build();

        storage = Storage.builder()
                .id(1)
                .name("Sochi-str-1")
                .region(region)
                .build();
        category = Category.builder().id(1).name("coffee").build();

        product = Product.builder()
                .id(1)
                .name("latte")
                .price(new BigDecimal(180))
                .category(category)
                .build();

        productDto = ProductDto.builder()
                .id(1)
                .name("latte")
                .price(new BigDecimal(180))
                .build();

        itemStorage = ItemStorage.builder()
                .id(10)
                .storage(storage)
                .product(product)
                .count(100)
                .build();
    }

    @Test
    void createItem() {
        when(itemStorageRepository.save(any(ItemStorage.class)))
                .thenReturn(itemStorage);

        ResponseDto result = underTest.createItem(itemStorage);

        verify(itemStorageRepository, times(1))
                .save(any(ItemStorage.class));

        assertThat(result.getMessage())
                .isEqualTo("Total count of product with id='1' on storage with id='1': 100");
        assertThat(result.getStatus()).isEqualTo("CREATED");
    }

    @Test
    @DisplayName("""
            get item by productId and storageId
            | empty optional
            | item does not exist
            """)
    void getItemByProductIdAndStorageId_emptyOptional_itemDoesNotExist() {
        Integer productId = 1;
        Integer storageId = 1;
        when(itemStorageRepository.findByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        Optional<ItemStorage> optionalResult = underTest.getItemByProductIdAndStorageId(productId, storageId);

        verify(itemStorageRepository, times(1))
                .findByProductIdAndStorageId(anyInt(), anyInt());

        assertThat(optionalResult).isEmpty();
    }

    @Test
    @DisplayName("""
            get item by productId and storageId
            | not empty optional
            | item exist
            """)
    void getItemByProductIdAndStorageId_optionalNotEmpty_itemExists() {
        Integer productId = 1;
        Integer storageId = 1;
        when(itemStorageRepository.findByProductIdAndStorageId(anyInt(), anyInt()))
                .thenReturn(Optional.of(itemStorage));

        Optional<ItemStorage> optionalResult = underTest.getItemByProductIdAndStorageId(productId, storageId);

        verify(itemStorageRepository, times(1))
                .findByProductIdAndStorageId(anyInt(), anyInt());

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getProduct().getName()).isEqualTo("latte");
        assertThat(optionalResult.get().getStorage().getName()).isEqualTo("Sochi-str-1");
    }

    @Test
    @DisplayName("""
            get products by storageId
            | empty result list
            | products do not exist
            """)
    void getProductsByStorageId_emptyResultList_productsDoNotExist() {
        Integer storageId = 10;
        when(itemStorageRepository.findProductsByStorageId(anyInt()))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getProductsByStorageId(storageId);

        verify(itemStorageRepository, times(1))
                .findProductsByStorageId(anyInt());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            get products by storageId
            | not empty result list
            | products exist
            """)
    void getProductsByStorageId_resultListNotEmpty_productsExist() {
        Integer storageId = 1;

        when(itemStorageRepository.findProductsByStorageId(anyInt()))
                .thenReturn(List.of(product));

        when(productMapper.toProductDtoList(anyList()))
                .thenReturn(List.of(productDto));

        List<ProductDto> result = underTest.getProductsByStorageId(storageId);

        verify(itemStorageRepository, times(1))
                .findProductsByStorageId(anyInt());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("latte");
    }

    @Test
    @DisplayName("""
            get products by storageId and category
            | empty result list
            | products do not exist
            """)
    void getProductsByStorageIdAndCategory_emptyResultList_productsDoNotExist() {
        Integer storageId = 10;
        String categoryName = "unknown";

        when(itemStorageRepository.findProductsByStorageIdAndCategory(anyInt(), anyString()))
                .thenReturn(List.of());

        List<ProductDto> result = underTest.getProductsByStorageIdAndCategory(storageId, categoryName);

        verify(itemStorageRepository, times(1))
                .findProductsByStorageIdAndCategory(anyInt(), anyString());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            get products by storageId and category
            | not empty result list
            | products exist
            """)
    void getProductsByStorageIdAndCategory_resultListIsNotEmpty_productsExist() {
        Integer storageId = 1;
        String categoryName = "coffee";

        when(itemStorageRepository.findProductsByStorageIdAndCategory(anyInt(), anyString()))
                .thenReturn(List.of(product));

        when(productMapper.toProductDtoList(anyList()))
                .thenReturn(List.of(productDto));

        List<ProductDto> result = underTest.getProductsByStorageIdAndCategory(storageId, categoryName);

        verify(itemStorageRepository, times(1))
                .findProductsByStorageIdAndCategory(anyInt(), anyString());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("latte");
    }

    @Test
    void createItemAndGetSaved() {
        when(itemStorageRepository.save(any(ItemStorage.class)))
                .thenReturn(itemStorage);

        ItemStorage result = underTest.createItemAndGetSaved(itemStorage);

        verify(itemStorageRepository, times(1))
                .save(any(ItemStorage.class));

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("""
            find storages for shipment
            | empty result list
            | products do not exist
            """)
    void findStoragesForShipment_emptyResultList_productsDoNotExist() {
        Integer storageId = 1;
        Integer regionId = 1;
        Integer count = 200;

        when(itemStorageRepository.findStorageForShipment(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of());

        List<Storage> result = underTest.findStoragesForShipment(storageId, regionId, count);

        verify(itemStorageRepository, times(1))
                .findStorageForShipment(anyInt(), anyInt(), anyInt());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            find storages for shipment
            | not empty result list
            | products exist
            """)
    void findStoragesForShipment_resultListIsNotEmpty_productsExist() {
        Integer storageId = 1;
        Integer regionId = 1;
        Integer count = 200;

        when(itemStorageRepository.findStorageForShipment(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(storage));

        List<Storage> result = underTest.findStoragesForShipment(storageId, regionId, count);

        verify(itemStorageRepository, times(1))
                .findStorageForShipment(anyInt(), anyInt(), anyInt());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Sochi-str-1");
    }


    @Test
    @DisplayName("""
            find storages for Loading or shipment
            | noFoundElementException
            | product does not exist
            """)
    void findStorageForLoadingOrShipment_noFoundElementException_productDoesNotExist() {
        LoadingShipmentRequest loadingShipmentRequest = LoadingShipmentRequest.builder()
                .productId(1)
                .region("Sochi")
                .count(10)
                .type(OperationType.SHIPMENT)
                .build();

        doThrow(NoFoundElementException.class)
                .when(productService)
                .checkExistAndGetProductById(anyInt());

        assertThrows(NoFoundElementException.class,
                () -> underTest.findStorageForLoadingOrShipment(loadingShipmentRequest));
    }

    @Test
    @DisplayName("""
            find storages for Loading or shipment
            | noFoundElementException
            | product exist and region does not exist
            """)
    void findStorageForLoadingOrShipment_noFoundElementException_productExistAndRegionDoesNotExist() {
        LoadingShipmentRequest loadingShipmentRequest = LoadingShipmentRequest.builder()
                .productId(1)
                .region("Sochi")
                .count(10)
                .type(OperationType.SHIPMENT)
                .build();

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(regionService.getRegionWithStoragesByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class,
                () -> underTest.findStorageForLoadingOrShipment(loadingShipmentRequest));
    }

    @Test
    @DisplayName("""
            find storages for Loading or shipment
            | correct response
            | product, region, storage exist
            """)
    void findStorageForLoadingOrShipment_correctResponse_productAndRegionExistAndStoragesExist() {
        LoadingShipmentRequest loadingShipmentRequest = LoadingShipmentRequest.builder()
                .productId(1)
                .region("Sochi")
                .count(10)
                .type(OperationType.LOADING)
                .build();

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(regionService.getRegionWithStoragesByName(anyString()))
                .thenReturn(Optional.of(region));

        when(storageMapper.toStorageDtoList(anyList()))
                .thenReturn(List.of(StorageDto.builder()
                        .id(1)
                        .name("Sochi-str-1")
                        .build()));

        underTest.findStorageForLoadingOrShipment(loadingShipmentRequest);

        verify(itemStorageRepository, times(0))
                .findStorageForShipment(anyInt(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("""
            find storages for Loading or shipment
            | correct response with empty list storages
            | product and region exist, storages do not exist
            """)
    void findStorageForLoadingOrShipment_correctResponseWithEmptyListStorages_productAndRegionExistAndStoragesDoNotExist() {
        LoadingShipmentRequest loadingShipmentRequest = LoadingShipmentRequest.builder()
                .productId(1)
                .region("Sochi")
                .count(10)
                .type(OperationType.SHIPMENT)
                .build();

        when(productService.checkExistAndGetProductById(anyInt()))
                .thenReturn(product);

        when(regionService.getRegionWithStoragesByName(anyString()))
                .thenReturn(Optional.of(region));

        when(storageMapper.toStorageDtoList(anyList()))
                .thenReturn(List.of(StorageDto.builder()
                        .id(1)
                        .name("Sochi-str-1")
                        .build()));

        underTest.findStorageForLoadingOrShipment(loadingShipmentRequest);

        verify(itemStorageRepository, times(1))
                .findStorageForShipment(anyInt(), anyInt(), anyInt());
    }
}