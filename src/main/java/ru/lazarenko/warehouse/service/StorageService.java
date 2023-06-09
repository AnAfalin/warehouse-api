package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.*;
import ru.lazarenko.warehouse.entity.*;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.exception.ProductCountException;
import ru.lazarenko.warehouse.model.TypeOperation;
import ru.lazarenko.warehouse.repository.StorageRepository;
import ru.lazarenko.warehouse.service.mapper.StorageMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final RegionService regionService;
    private final ProductService productService;
    private final ItemStorageService itemStorageService;
    private final CategoryService categoryService;
    private final StorageMapper storageMapper;
    private final OperationHistoryService operationHistoryService;
    private final ManufactureAnalysisService manufactureAnalysisService;

    @Transactional
    public ResponseDto createWarehouse(StorageDto request) {
        Storage storage = storageMapper.toStorage(request);

        checkUniqueName(request.getName());

        Region region = regionService.checkExistAndGetRegionByName(request.getRegion().getName());
        storage.setRegion(region);

        Storage savedStorage = storageRepository.save(storage);

        return ResponseDto.builder()
                .status(HttpStatus.CREATED.toString())
                .message("Storage successful created: id='%s'".formatted(savedStorage.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<StorageDto> getAllWarehouse() {
        List<Storage> storages = storageRepository.findAll();

        return storageMapper.toStorageDtoList(storages);
    }

    @Transactional
    public ResponseDto increaseProductInStorage(ChangingCountItemStorageDto request) {
        Storage storage = checkExistAndGetStorageById(request.getStorageId());
        Product product = productService.checkExistAndGetProductById(request.getProductId());

        if (request.getCount() <= 0) {
            throw new ProductCountException("Count '%s' is not correct".formatted(request.getCount()));
        }

        Optional<ItemStorage> optionalItem = itemStorageService.getItemByProductIdAndStorageId(product.getId(), storage.getId());

        if (optionalItem.isEmpty()) {
            ItemStorage itemStorage = ItemStorage.builder()
                    .product(product)
                    .storage(storage)
                    .count(request.getCount())
                    .build();

            operationHistoryService.saveOperationHistory(request.getCount(), product, storage, TypeOperation.LOADING);

            return itemStorageService.createItem(itemStorage);
        }

        ItemStorage item = optionalItem.get();

        int newCount = item.getCount() + request.getCount();
        operationHistoryService.saveOperationHistory(request.getCount(), product, storage, TypeOperation.LOADING);

        item.setCount(newCount);
        return itemStorageService.createItem(item);
    }

    @Transactional
    public ResponseDto decreaseProductInStorage(ChangingCountItemStorageDto request) {
        Storage storage = checkExistAndGetStorageById(request.getStorageId());
        Product product = productService.checkExistAndGetProductById(request.getProductId());

        ItemStorage foundItem = itemStorageService.getItemByProductIdAndStorageId(product.getId(), storage.getId())
                .orElseThrow(() -> new NoFoundElementException("Product with id='%s' in storage with id='%s' not found"));

        if (request.getCount() <= 0) {
            throw new ProductCountException("Count '%s' is not correct".formatted(request.getCount()));
        }

        if (foundItem.getCount() < request.getCount()) {
            throw new ProductCountException("Count of product with id='%s' less than %s. Actual count of product is %s"
                    .formatted(foundItem.getProduct().getId(), request.getCount(), foundItem.getCount()));
        }

        int newCount = foundItem.getCount() - request.getCount();

        operationHistoryService.saveOperationHistory(request.getCount(), product, storage, TypeOperation.SHIPMENT);

        foundItem.setCount(newCount);
        ItemStorage savedItem = itemStorageService.createItemAndGetSaved(foundItem);

        return ResponseDto.builder()
                .status(HttpStatus.OK.toString())
                .message("Total count of product with id='%s' on storage with id='%s': %s"
                        .formatted(savedItem.getProduct().getId(), savedItem.getStorage().getId(),
                                savedItem.getCount()))
                .build();
    }

    @Transactional(readOnly = true)
    public Storage checkExistAndGetStorageById(Integer id) {
        return storageRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Storage with id='%s' not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public void checkUniqueName(String name) {
        Optional<Storage> foundStorage = storageRepository.findByName(name);
        if (foundStorage.isPresent()) {
            throw new NoUniqueObjectException("Storage with name='%s' already exist".formatted(name));
        }
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductsByStorageId(Integer storageId) {
        Storage storage = checkExistAndGetStorageById(storageId);

        return itemStorageService.getProductsByStorageId(storage.getId());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductsByStorageIdAndCategory(Integer storageId, String category) {
        Storage storage = checkExistAndGetStorageById(storageId);
        Category foundCategory = categoryService.checkExistAndGetCategoryByName(category);

        return itemStorageService.getProductsByStorageIdAndCategory(storage.getId(), foundCategory.getName());
    }

    @Transactional(readOnly = true)
    public LoadingShipmentResponse findStorageForLoadingOrShipment(Integer productId, String region, Integer count,
                                                                   String typeOperation)  {
        Product product = productService.checkExistAndGetProductById(productId);

        TypeOperation type;
        if (typeOperation.equals(TypeOperation.LOADING.name())) {
            type = TypeOperation.LOADING;
        } else {
            type = TypeOperation.SHIPMENT;
        }

        Optional<Region> optionalRegion = regionService.getWithStoragesByName(region);
        if (optionalRegion.isEmpty()) {
            throw new NoFoundElementException("Storage is missing in region %s. %s is not possible.".
                    formatted(region, type.name()));
        }

        List<StorageDto> storageDtos;
        if (type.equals(TypeOperation.LOADING)) {
            storageDtos = storageMapper.toStorageDtoList(optionalRegion.get().getStorages());
            return LoadingShipmentResponse.builder()
                    .storages(storageDtos)
                    .build();
        }

        List<Storage> storageForShipment = itemStorageService.findStorageForShipment(product.getId(),
                optionalRegion.get().getId(), count);
        storageDtos = storageMapper.toStorageDtoList(storageForShipment);
        return LoadingShipmentResponse.builder()
                .storages(storageDtos)
                .build();


    }

}
