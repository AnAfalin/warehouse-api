package ru.lazarenko.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.lazarenko.warehouse.entity.Region;

@Getter
@Setter
public class RegionDto {
    private Integer id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;
}
