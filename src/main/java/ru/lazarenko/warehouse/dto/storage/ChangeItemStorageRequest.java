package ru.lazarenko.warehouse.dto.storage;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class ChangeItemStorageRequest {
    @NotNull(message = "Product id cannot null")
    private Integer productId;

    @NotNull(message = "Storage id cannot null")
    private Integer storageId;

    @Min(value = 0, message = "Count cannot less than 1")
    private Integer count;
}
