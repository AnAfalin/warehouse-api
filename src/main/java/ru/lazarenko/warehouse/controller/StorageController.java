package ru.lazarenko.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.ChangeItemStorageRequest;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentRequest;
import ru.lazarenko.warehouse.dto.storage.LoadingShipmentResponse;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.service.StorageService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/storages")
public class StorageController {
    private final StorageService storageService;

    @PostMapping
    public ResponseDto addStorage(@RequestBody @Valid StorageDto request) {
        log.info("Request for create warehouse");
        return storageService.createStorage(request);
    }

    @GetMapping
    public List<StorageDto> getAllStorages() {
        return storageService.getAllStorages();
    }

    @PostMapping("/add-product")
    public ResponseDto addProductToStorage(@RequestBody @Valid ChangeItemStorageRequest request) {
        return storageService.increaseProductInStorage(request);
    }

    @PostMapping("/decrease-product")
    public ResponseDto decreaseProductToStorage(@RequestBody @Valid ChangeItemStorageRequest request) {
        return storageService.decreaseProductInStorage(request);
    }

    @GetMapping("/{storageId}/products")
    public List<ProductDto> getAllProductsByStorage(@RequestParam(required = false, name = "category") String category,
                                                    @PathVariable Integer storageId) {
        if (!StringUtils.hasLength(category)) {
            return storageService.getAllProductsByStorageId(storageId);
        }
        return storageService.getAllProductsByStorageIdAndCategory(storageId, category);
    }

    @GetMapping("/find")
    public LoadingShipmentResponse getStorageForLoadingShipment(LoadingShipmentRequest request) {
        return storageService.findStorageForLoadingOrShipment(request);
    }
}
