package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.Category;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository underTest;

    @Test
    @DisplayName("find by name | optional not empty | category exists")
    void findByName_optionalNotEmpty_categoryExists() {
        String category = "coffee";

        Optional<Category> optionalResult = underTest.findByName(category);
        
        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getName()).isEqualTo("coffee");
        assertThat(optionalResult.get().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("find by name | optional empty | category does not exist")
    void findByName_optionalNotEmpty_categoryDoesNotExist() {
        String category = "unknown";

        Optional<Category> optionalResult = underTest.findByName(category);

        assertThat(optionalResult).isEmpty();
    }
}