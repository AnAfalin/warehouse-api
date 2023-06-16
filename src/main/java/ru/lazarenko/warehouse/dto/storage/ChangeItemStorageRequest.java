package ru.lazarenko.warehouse.dto.storage;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeItemStorageRequest {
    @NotNull(message = "Product id cannot null")
    private Integer productId;

    @NotNull(message = "Storage id cannot null")
    private Integer storageId;

    @NotNull(message = "Count cannot null")
    @Min(value = 0, message = "Count cannot less than 0")
    private Integer count;
}
