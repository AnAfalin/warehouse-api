package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.entity.OperationHistory;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.TypeOperation;
import ru.lazarenko.warehouse.repository.OperationHistoryRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationHistoryService {
    private final OperationHistoryRepository operationHistoryRepository;

    @Transactional
    public void saveOperationHistory(Integer count, Product product, Storage storage,
                                     TypeOperation typeOperation) {
        OperationHistory operationHistory = new OperationHistory();
        operationHistory.setOperation(typeOperation);
        operationHistory.setCount(count);
        operationHistory.setProduct(product);
        operationHistory.setStorage(storage);
        operationHistory.setDate(LocalDateTime.now());

        operationHistoryRepository.save(operationHistory);
    }
}
