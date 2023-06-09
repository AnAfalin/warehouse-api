package ru.lazarenko.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageDto {
    private Integer id;
    private String name;
    private RegionDto region;
}
