package ru.lazarenko.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateDto {

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotNull(message = "Price cannot be  null")
    private BigDecimal price;

    private CategoryDto category;
}
