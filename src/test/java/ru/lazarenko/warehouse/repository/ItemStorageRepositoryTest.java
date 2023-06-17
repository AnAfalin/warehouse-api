package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.ItemStorage;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ItemStorageRepositoryTest {
    @Autowired
    ItemStorageRepository underTest;

    @Test
    @DisplayName("""
            find by productId and storageId
            | optional result is empty
            | itemStorage does not exist
            """)
    void findByProductIdAndStorageId_optionalResultIsEmpty_itemStorageDoesNotExist() {
        Integer productId = 10;
        Integer storageId = 10;

        Optional<ItemStorage> optionalResult = underTest.findByProductIdAndStorageId(productId, storageId);

        assertThat(optionalResult).isEmpty();
    }

    @Test
    @DisplayName("""
            find by productId and storageId
            | optional result is not empty
            | itemStorage does not exist
            """)
    void findByProductIdAndStorageId_optionalResultIsNotEmpty_itemStorageDoesNotExist() {
        Integer productId = 1;
        Integer storageId = 2;

        Optional<ItemStorage> optionalResult = underTest.findByProductIdAndStorageId(productId, storageId);

        assertThat(optionalResult).isNotEmpty();

        ItemStorage result = optionalResult.get();
        assertAll(
                () -> assertThat(result.getProduct().getId()).isEqualTo(1),
                () -> assertThat(result.getStorage().getId()).isEqualTo(2),
                () -> assertThat(result.getCount()).isEqualTo(25)
        );
    }

    @Test
    @DisplayName("""
            find products by storageId
            | result list is empty
            | products do not exist in storage
            """)
    void findProductsByStorageId_resultIsEmpty_productsDoNotExistInStorage() {
        Integer storageId = 4;
        List<Product> result = underTest.findProductsByStorageId(storageId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            find products by storageId
            | result list is not empty
            | products do exist in storage
            """)
    void findProductsByStorageId_resultIsNotEmpty_productsExistInStorage() {
        Integer storageId = 3;
        List<Product> result = underTest.findProductsByStorageId(storageId);

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getName()).isEqualTo("green tea")
        );
    }

    @Test
    @DisplayName("""
            find products by storageId and category
            | result list is empty
            | products with same category do not exist in storage
            """)
    void findProductsByStorageIdAndCategory_resultIsEmpty_productsDoNotExistInStorage() {
        Integer storageId = 10;
        String category = "unknown";

        List<Product> result = underTest.findProductsByStorageIdAndCategory(storageId, category);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            find products by storageId and category
            | result list is not empty
            | products with same category do not exist in storage
            """)
    void findProductsByStorageIdAndCategory_resultIsNotEmpty_productsExistInStorage() {
        Integer storageId = 3;
        String category = "coffee";

        List<Product> result = underTest.findProductsByStorageIdAndCategory(storageId, category);

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("cappuccino")
        );
    }

    @Test
    @DisplayName("""
            find products by storageId and category
            | result list is empty
            | products with same category do not exist in storage
            """)
    void findStorageForShipment_resultIsEmpty_productsDoNotExistInStorage() {
        Integer productId = 10;
        Integer regionId = 10;
        Integer count = 100;

        List<Storage> result = underTest.findStorageForShipment(productId, regionId, count);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            find products by storageId and category
            | result list is not empty
            | products with same category do not exist in storage
            """)
    void findStorageForShipment_resultIsNotEmpty_productsExistInStorage() {
        Integer productId = 1;
        Integer regionId = 1;
        Integer count = 80;

        List<Storage> result = underTest.findStorageForShipment(productId, regionId, count);

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("Sochi-str")
        );
    }
}