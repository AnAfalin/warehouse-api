package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    UserRepository underTest;

    @Test
    @DisplayName("find by name | optional not empty | user exists")
    void findByName_optionalNotEmpty_userExists() {
        String username = "admin";

        Optional<User> optionalResult = underTest.findByUsername(username);

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getUsername()).isEqualTo("admin");
        assertThat(optionalResult.get().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("find by name | optional empty | user does not exist")
    void findByName_optionalNotEmpty_userDoesNotExist() {
        String username = "unknown";

        Optional<User> optionalResult = underTest.findByUsername(username);

        assertThat(optionalResult).isEmpty();
    }
}