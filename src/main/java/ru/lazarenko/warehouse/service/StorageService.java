package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.*;
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

    @Transactional
    public ResponseDto createStorage(StorageDto request) {
        Storage storage = storageMapper.toStorage(request);

        checkUniqueName(request.getName());

        Region region = regionService.checkExistAndGetRegionByName(request.getRegion().getName());
        storage.setRegion(region);

        Storage savedStorage = storageRepository.save(storage);
        log.info("Storage successful created: {}", savedStorage);

        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Storage successful created: id='%s'".formatted(savedStorage.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<StorageDto> getAllStorages() {
        List<Storage> storages = storageRepository.findAll();

        return storageMapper.toStorageDtoList(storages);
    }

    @Transactional
    public ResponseDto increaseProductInStorage(ChangeItemStorageRequest request) {
        Storage storage = checkExistAndGetStorageById(request.getStorageId());
        Product product = productService.checkExistAndGetProductById(request.getProductId());

        Optional<ItemStorage> optionalItem = itemStorageService.getItemByProductIdAndStorageId(product.getId(), storage.getId());

        OperationHistoryDto operationHistoryDto = OperationHistoryDto.builder()
                .product(product)
                .storage(storage)
                .count(request.getCount())
                .operation(OperationType.LOADING)
                .build();
        operationHistoryService.saveOperationHistory(operationHistoryDto);

        if (optionalItem.isEmpty()) {
            ItemStorage itemStorage = ItemStorage.builder()
                    .product(product)
                    .storage(storage)
                    .count(request.getCount())
                    .build();

            return itemStorageService.createItem(itemStorage);
        }

        ItemStorage item = optionalItem.get();

        int newCount = item.getCount() + request.getCount();
        item.setCount(newCount);

        return itemStorageService.createItem(item);
    }

    @Transactional
    public ResponseDto decreaseProductInStorage(ChangeItemStorageRequest request) {
        Storage storage = checkExistAndGetStorageById(request.getStorageId());
        Product product = productService.checkExistAndGetProductById(request.getProductId());

        ItemStorage foundItem = itemStorageService.getItemByProductIdAndStorageId(product.getId(), storage.getId())
                .orElseThrow(() -> new NoFoundElementException("Product with id='%s' in storage with id='%s' not found"));

        if (foundItem.getCount() < request.getCount()) {
            throw new ProductCountException("Count of product with id='%s' less than %s. Actual count of product is %s"
                    .formatted(foundItem.getProduct().getId(), request.getCount(), foundItem.getCount()));
        }

        int newCount = foundItem.getCount() - request.getCount();
        foundItem.setCount(newCount);

        OperationHistoryDto operationHistoryDto = OperationHistoryDto.builder()
                .product(product)
                .storage(storage)
                .count(request.getCount())
                .operation(OperationType.SHIPMENT)
                .build();
        operationHistoryService.saveOperationHistory(operationHistoryDto);

        ItemStorage savedItem = itemStorageService.updateItemAndGetSaved(foundItem);

        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
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
}