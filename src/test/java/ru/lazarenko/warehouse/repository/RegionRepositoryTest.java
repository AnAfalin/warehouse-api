package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.Region;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class RegionRepositoryTest {
    @Autowired
    RegionRepository underTest;

    @Test
    @DisplayName("find by name | optional not empty | region exists")
    void findByName_optionalNotEmpty_regionExists() {
        String region = "Moscow";

        Optional<Region> optionalResult = underTest.findByName(region);

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getName()).isEqualTo("Moscow");
        assertThat(optionalResult.get().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("find by name | optional empty | region does not exist")
    void findByName_optionalNotEmpty_regionDoesNotExist() {
        String region = "unknown";

        Optional<Region> optionalResult = underTest.findByName(region);

        assertThat(optionalResult).isEmpty();
    }

    @Test
    @DisplayName("find by name | optional not empty and object contains not empty list storages | region exists")
    void findWithStoragesByName_optionalNotEmptyAndContainsListStorages_regionExists() {
        String region = "Sochi";

        Optional<Region> optionalResult = underTest.findByName(region);

        assertThat(optionalResult).isNotEmpty();
        Region result = optionalResult.get();
        assertThat(result.getName()).isEqualTo("Sochi");
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStorages().size()).isEqualTo(1);
        assertThat(result.getStorages().get(0).getName()).isEqualTo("Sochi-str");
    }

    @Test
    @DisplayName("find by name | optional empty | region does not exist")
    void findWithStoragesByName_optionalNotEmpty_regionDoesNotExist() {
        String region = "unknown";

        Optional<Region> optionalResult = underTest.findByName(region);

        assertThat(optionalResult).isEmpty();
    }
}