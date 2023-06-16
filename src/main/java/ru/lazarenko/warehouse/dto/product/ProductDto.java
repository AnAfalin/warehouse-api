package ru.lazarenko.warehouse.dto.product;

import lombok.*;
import ru.lazarenko.warehouse.dto.CategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Integer id;

    @NotBlank(message = "Product name cannot be empty or null")
    private String name;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @Valid
    @NotNull(message = "Category cannot be null")
    private CategoryDto category;
}
