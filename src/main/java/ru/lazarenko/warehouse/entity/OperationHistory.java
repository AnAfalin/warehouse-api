package ru.lazarenko.warehouse.entity;

import jakarta.persistence.*;
import ru.lazarenko.warehouse.model.TypeOperation;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operation_histories")
public class OperationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private TypeOperation operation;

    private Integer startCount;

    private Integer operationCount;

    private Integer endCount;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private List<Product> products = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private List<Storage> storages = new ArrayList<>();
}
