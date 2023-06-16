package ru.lazarenko.warehouse.dto.storage;

import lombok.*;
import ru.lazarenko.warehouse.model.OperationType;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoadingShipmentRequest {
    @NotBlank(message = "Product id cannot be null or empty")
    private Integer productId;

    @NotBlank(message = "Region cannot be null or empty")
    private String region;

    @NotBlank(message = "Count pf product cannot be null or empty")
    private Integer count;

    @NotBlank(message = "Type of operation(loading / shipment) be null or empty")
    private OperationType type;
}
