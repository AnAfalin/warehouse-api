package ru.lazarenko.warehouse.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Integer id;

    @NotBlank(message = "Category name cannot be empty or null")
    private String name;
}
