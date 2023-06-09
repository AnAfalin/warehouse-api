package ru.lazarenko.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceRangeDto {

    @NotNull(message = "Min price cannot be null")
    @Min(value = 0, message = "Min price cannot be less than 0")
    private Integer min;

    @NotNull(message = "Max price cannot be null")
    @Min(value = 100, message = "Max price cannot be less than 100")
    private Integer max;
}
