package ru.lazarenko.warehouse.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto {
    private Integer id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;
}
