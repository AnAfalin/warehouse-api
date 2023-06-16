package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(value = "select p from Product p where p.category.id=:id")
    List<Product> findAllByCategoryId(Integer id);

    @Query(value = "select p from Product p where p.price >= :min and p.price <= :max")
    List<Product> findProductsByMinAndMaxPrice(BigDecimal min, BigDecimal max);

}
