package ru.lazarenko.warehouse.dto.product;

import lombok.Getter;
import lombok.Setter;
import ru.lazarenko.warehouse.dto.CategoryDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
