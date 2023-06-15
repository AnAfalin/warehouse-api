package ru.lazarenko.warehouse.dto.storage;

import lombok.Getter;
import lombok.Setter;
import ru.lazarenko.warehouse.dto.RegionDto;

@Getter
@Setter
public class StorageDto {
    private Integer id;
    private String name;
    private RegionDto region;
}
