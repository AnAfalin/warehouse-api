package ru.lazarenko.warehouse.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.lazarenko.warehouse.entity.OperationHistory;
import ru.lazarenko.warehouse.model.OperationType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class OperationHistoryRepositoryTest {
    @Autowired
    OperationHistoryRepository underTest;

    @Test
    @DisplayName("find all by date is after | empty result list | operations do not exist")
    void findAllByDateIsAfter_emptyResultList_operationsDoNotExist() {
        LocalDateTime date = LocalDateTime.now();
        List<OperationHistory> result = underTest.findAllByDateIsAfter(date);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("find all by date is after | result list not empty| operations exist")
    void findAllByDateIsAfter_resultListNotEmpty_operationsExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 12, 0, 0, 0);
        List<OperationHistory> result = underTest.findAllByDateIsAfter(date);

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(4),
                () -> assertThat(result.stream()
                        .filter(el -> el.getOperation().equals(OperationType.SHIPMENT)).toList()
                        .size()).isEqualTo(0)
                );
    }
}