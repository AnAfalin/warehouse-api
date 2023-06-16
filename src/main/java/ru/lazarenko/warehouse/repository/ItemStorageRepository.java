package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.ItemStorage;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemStorageRepository extends JpaRepository<ItemStorage, Integer> {

    @Query(value = "select i from ItemStorage i where i.product.id=:productId and i.storage.id=:storageId")
    Optional<ItemStorage> findByProductIdAndStorageId(Integer productId, Integer storageId);

    @Query(value = "select p from ItemStorage its left join its.product p where its.storage.id=:storageId")
    List<Product> findProductsByStorageId(Integer storageId);

    @Query(value = "select p from ItemStorage its left join its.product p where its.storage.id=:storageId and p.category.name=:category")
    List<Product> findProductsByStorageIdAndCategory(Integer storageId, String category);

    @Query(value = "select s from ItemStorage its inner join its.storage s " +
            "where s.region.id = :regionId and its.product.id = :productId and its.count >= :count")
    List<Storage> findStorageForShipment(Integer productId, Integer regionId, Integer count);
}
