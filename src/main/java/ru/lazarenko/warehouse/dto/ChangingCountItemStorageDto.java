package ru.lazarenko.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangingCountItemStorageDto {
    private Integer productId;
    private Integer storageId;
    private Integer count;
}
