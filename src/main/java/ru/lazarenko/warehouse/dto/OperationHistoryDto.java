package ru.lazarenko.warehouse.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.TypeOperation;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationHistoryDto {
    private TypeOperation operation;

    private Integer count;

    private LocalDateTime date;

    private Product product;

    private Storage storage;
}
