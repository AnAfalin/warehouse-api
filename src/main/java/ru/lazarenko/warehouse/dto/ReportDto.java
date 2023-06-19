package ru.lazarenko.warehouse.dto;

import lombok.*;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.ChangeType;
import ru.lazarenko.warehouse.model.OperationType;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Integer id;

    private OperationType operation;

    private ChangeType changeType;

    private LocalDate reportDate;

    private ProductDto product;

    private StorageDto storage;
}
