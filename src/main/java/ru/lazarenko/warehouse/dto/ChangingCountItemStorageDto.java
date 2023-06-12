package ru.lazarenko.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangingCountItemStorageDto {
    @NotNull(message = "Product id cannot null")
    private Integer productId;

    @NotNull(message = "Storage id cannot null")
    private Integer storageId;

    @Min(value = 0, message = "Count cannot less than 1")
    private Integer count;
}
