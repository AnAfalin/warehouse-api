package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "select p from ItemStorage its left join Product p where its.storage.id=:id")
    List<Product> findProductsByStorageId(Integer id);

    @Query(value = "select p from ItemStorage its left join Product p where its.storage.id=:id and p.category.name=:category")
    List<Product> findProductsByStorageIdAndCategory(Integer id, String category);

    @Query(value = "SELECT s FROM ItemStorage its INNER JOIN its.storage s " +
            "WHERE s.region.id = :regionId AND its.product.id = :productId AND its.count >= :count")
    List<Storage> findStorageForShipment(Integer productId, Integer regionId, Integer count);
}
