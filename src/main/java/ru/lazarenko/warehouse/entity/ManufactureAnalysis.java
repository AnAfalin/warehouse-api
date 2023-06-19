package ru.lazarenko.warehouse.entity;

import lombok.*;
import ru.lazarenko.warehouse.model.ChangeType;
import ru.lazarenko.warehouse.model.OperationType;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "manufacture_analyses")
public class ManufactureAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private OperationType operation;

    @Enumerated(value = EnumType.STRING)
    private ChangeType changeType;

    private LocalDate reportDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

}
