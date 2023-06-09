package ru.lazarenko.warehouse.entity;

import jakarta.persistence.*;
import ru.lazarenko.warehouse.model.TypeNeedChange;
import ru.lazarenko.warehouse.model.TypeOperation;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "manufacture_analyses")
public class ManufactureAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private TypeOperation typeOperation;

    private TypeNeedChange typeNeedChange;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private List<Product> products = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private List<Storage> storages = new ArrayList<>();

}
