package ru.lazarenko.warehouse.dto.storage;

import lombok.*;
import ru.lazarenko.warehouse.dto.RegionDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageDto {
    private Integer id;

    @NotBlank(message = "Storage name cannot be empty or null")
    private String name;

    @Valid
    @NotNull(message = "Region cannot be null")
    private RegionDto region;
}
