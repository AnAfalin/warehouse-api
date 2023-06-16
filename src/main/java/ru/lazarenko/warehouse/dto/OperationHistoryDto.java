package ru.lazarenko.warehouse.dto;

import lombok.*;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.OperationType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationHistoryDto {
    private OperationType operation;

    private Integer count;

    private LocalDateTime date;

    private Product product;

    private Storage storage;
}
