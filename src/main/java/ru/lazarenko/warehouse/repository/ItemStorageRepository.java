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

    @Query(value = "select p from ItemStorage is left join Product p where is.storage.id=:id")
    List<Product> findProductsByStorageId(Integer id);

    @Query(value = "select p from ItemStorage is left join Product p where is.storage.id=:id and p.category.name=:category")
    List<Product> findProductsByStorageIdAndCategory(Integer id, String category);

    @Query(value = "select s from ItemStorage is left join is.storage s " +
            "where s.region.id =:id1 and is.product.id=:id and is.count >= :count")
    List<Storage> findStorageForShipment(Integer id, Integer id1, Integer count);
}
