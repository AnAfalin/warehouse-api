package ru.lazarenko.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.lazarenko.warehouse.model.TypeNeedChange;
import ru.lazarenko.warehouse.model.TypeOperation;

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
    private TypeOperation typeOperation;

    @Enumerated(value = EnumType.STRING)
    private TypeNeedChange typeNeedChange;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

}
