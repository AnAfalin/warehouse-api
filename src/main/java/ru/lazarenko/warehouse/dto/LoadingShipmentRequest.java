package ru.lazarenko.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadingShipmentRequest {
    private Integer productId;
    private Integer count;
    private RegionDto region;
}
