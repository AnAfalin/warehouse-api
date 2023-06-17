package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentRequest;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentResponse;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.ItemStorage;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Region;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.repository.ItemStorageRepository;
import ru.lazarenko.warehouse.service.mapper.ProductMapper;
import ru.lazarenko.warehouse.service.mapper.StorageMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemStorageService {
    private final ItemStorageRepository itemStorageRepository;
    private final ProductService productService;
    private final RegionService regionService;
    private final ProductMapper productMapper;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    public Optional<ItemStorage> getItemByProductIdAndStorageId(Integer productId, Integer storageId) {
        return itemStorageRepository.findByProductIdAndStorageId(productId, storageId);
    }

    @Transactional
    public ResponseDto createItem(ItemStorage itemStorage) {
        ItemStorage savedItem = itemStorageRepository.save(itemStorage);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Total count of product with id='%s' on storage with id='%s': %s"
                        .formatted(savedItem.getProduct().getId(), savedItem.getStorage().getId(),
                                savedItem.getCount()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByStorageId(Integer id) {
        List<Product> products = itemStorageRepository.findProductsByStorageId(id);
        return productMapper.toProductDtoList(products);
    }


    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByStorageIdAndCategory(Integer id, String category) {
        List<Product> products = itemStorageRepository.findProductsByStorageIdAndCategory(id, category);
        return productMapper.toProductDtoList(products);
    }

    @Transactional(readOnly = true)
    public List<Storage> findStoragesForShipment(Integer productId, Integer regionId, Integer count) {
        return itemStorageRepository.findStorageForShipment(productId, regionId, count);
    }

    @Transactional
    public ItemStorage createItemAndGetSaved(ItemStorage item) {
        return itemStorageRepository.save(item);
    }

    @Transactional(readOnly = true)
    public LoadingShipmentResponse findStorageForLoadingOrShipment(LoadingShipmentRequest request)  {
        Product product = productService.checkExistAndGetProductById(request.getProductId());

        Optional<Region> optionalRegion = regionService.getRegionWithStoragesByName(request.getRegion());
        if (optionalRegion.isEmpty()) {
            throw new NoFoundElementException("Storage is missing in region %s. %s is not possible.".
                    formatted(request.getRegion(), request.getType().name()));
        }

        List<StorageDto> storageDtos;
        if (request.getType().equals(OperationType.LOADING)) {
            storageDtos = storageMapper.toStorageDtoList(optionalRegion.get().getStorages());
            return LoadingShipmentResponse.builder()
                    .storages(storageDtos)
                    .build();
        }

        List<Storage> storageForShipment = findStoragesForShipment(product.getId(),
                optionalRegion.get().getId(), request.getCount());
        storageDtos = storageMapper.toStorageDtoList(storageForShipment);
        return LoadingShipmentResponse.builder()
                .storages(storageDtos)
                .build();

    }
}
