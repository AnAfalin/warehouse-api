package ru.lazarenko.warehouse.dto.product;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRangeDto {

    @NotNull(message = "Min price cannot be null")
    @Min(value = 0, message = "Min price cannot be less than 0")
    private Integer min;

    @NotNull(message = "Max price cannot be null")
    @Min(value = 100, message = "Max price cannot be less than 100")
    private Integer max;
}
