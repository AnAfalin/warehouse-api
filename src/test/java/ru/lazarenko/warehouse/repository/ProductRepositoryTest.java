package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ProductRepositoryTest {
    @Autowired
    ProductRepository underTest;

    @Test
    @DisplayName("""
            find products by price between min and max price
            | result list not empty and contains two products
            | products found
            """)
    void findProductsByMinAndMaxPrice_notEmptyResultList_productsFound() {
        BigDecimal minPrice = new BigDecimal(150);
        BigDecimal maxPrice = new BigDecimal(200);

        List<Product> result = underTest.findProductsByMinAndMaxPrice(minPrice, maxPrice);

        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getName()).isEqualTo("herbal tea"),
                () -> assertThat(result.get(1).getName()).isEqualTo("cappuccino")
        );
    }

    @Test
    @DisplayName("""
            find products by price between min and max price
            | result list is empty
            | products not found
            """)
    void findProductsByMinAndMaxPrice_emptyResultList_productsNotFound() {
        BigDecimal minPrice = new BigDecimal(1000);
        BigDecimal maxPrice = new BigDecimal(2000);

        List<Product> result = underTest.findProductsByMinAndMaxPrice(minPrice, maxPrice);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("""
            find all by category id
            | result list not empty and contains two products
            | products found
            """)
    void findAllByCategoryId_notEmptyResultList_productsFound() {
        Integer categoryId = 1;
        List<Product> result = underTest.findAllByCategoryId(categoryId);

        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getName()).isEqualTo("americano"),
                () -> assertThat(result.get(1).getName()).isEqualTo("cappuccino")
        );
    }

    @Test
    @DisplayName("""
            find all by category id
            | result list is empty
            | products not found
            """)
    void findAllByCategoryId_emptyResultList_productsNotFound() {
        Integer categoryId = 3;
        List<Product> result = underTest.findAllByCategoryId(categoryId);

        assertThat(result).isEmpty();
    }

}