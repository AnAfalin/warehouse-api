package ru.lazarenko.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.lazarenko.warehouse.model.TypeOperation;

@Getter
@Setter
public class LoadingShipmentRequest {
    @NotBlank(message = "Product id cannot be null or empty")
    private Integer productId;

    @NotBlank(message = "Region cannot be null or empty")
    private String region;

    @NotBlank(message = "Count pf product cannot be null or empty")
    private Integer count;

    @NotBlank(message = "Type of operation(loading / shipment) be null or empty")
    private TypeOperation type;
}
