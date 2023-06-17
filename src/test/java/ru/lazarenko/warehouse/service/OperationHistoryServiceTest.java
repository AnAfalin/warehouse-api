package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.OperationHistoryDto;
import ru.lazarenko.warehouse.entity.*;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.repository.OperationHistoryRepository;
import ru.lazarenko.warehouse.service.mapper.OperationMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OperationHistoryServiceTest {
    @Autowired
    OperationHistoryService underTest;

    @MockBean
    OperationHistoryRepository operationHistoryRepository;

    @MockBean
    OperationMapper operationMapper;

    @Captor
    ArgumentCaptor<OperationHistory> captor;

    @Test
    @DisplayName("save operation history")
    void saveOperationHistory() {
        Category category = Category.builder().id(1).name("coffee").build();
        Product product = Product.builder()
                .id(1)
                .name("Americano")
                .price(new BigDecimal(100))
                .category(category)
                .build();

        Region region = Region.builder().id(1).name("Sochi").build();
        Storage storage = Storage.builder().name("Sochi-str-1").region(region).build();

        OperationHistoryDto operationDto = OperationHistoryDto.builder()
                .operation(OperationType.LOADING)
                .product(product)
                .storage(storage)
                .date(LocalDateTime.of(2023, 5, 10, 13, 0, 0))
                .count(25)
                .build();

        OperationHistory operation = OperationHistory.builder()
                .operation(OperationType.LOADING)
                .product(product)
                .storage(storage)
                .date(LocalDateTime.of(2023, 5, 10, 13, 0, 0))
                .count(25)
                .build();

        when(operationMapper.toOperationHistory(operationDto))
                .thenReturn(operation);

        underTest.saveOperationHistory(operationDto);

        verify(operationHistoryRepository, times(1))
                .save(any(OperationHistory.class));

        verify(operationHistoryRepository).save(captor.capture());
        OperationHistory value = captor.getValue();

        assertThat(value.getProduct().getName()).isEqualTo("Americano");
        assertThat(value.getDate()).isEqualTo(operationDto.getDate());
        assertThat(value.getOperation()).isEqualTo(OperationType.LOADING);
    }
}