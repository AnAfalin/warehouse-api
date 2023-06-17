package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.Storage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class StorageRepositoryTest {
    @Autowired
    StorageRepository underTest;

    @Test
    @DisplayName("find by name | optional not empty | storage exists")
    void findByName_optionalNotEmpty_storageExists() {
        String storage = "Moscow-str-1";

        Optional<Storage> optionalResult = underTest.findByName(storage);

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getName()).isEqualTo("Moscow-str-1");
        assertThat(optionalResult.get().getId()).isEqualTo(2);
        assertThat(optionalResult.get().getRegion().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("find by name | optional empty | storage does not exist")
    void findByName_optionalNotEmpty_storageDoesNotExist() {
        String storage = "unknown";

        Optional<Storage> optionalResult = underTest.findByName(storage);

        assertThat(optionalResult).isEmpty();
    }
}