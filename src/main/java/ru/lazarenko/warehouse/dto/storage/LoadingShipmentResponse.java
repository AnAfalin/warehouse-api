package ru.lazarenko.warehouse.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoadingShipmentResponse {
    private List<StorageDto> storages;
}
