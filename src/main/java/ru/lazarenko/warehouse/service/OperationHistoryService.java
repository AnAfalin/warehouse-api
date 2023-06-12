package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.OperationHistoryDto;
import ru.lazarenko.warehouse.repository.OperationHistoryRepository;
import ru.lazarenko.warehouse.service.mapper.OperationMapper;

@Service
@RequiredArgsConstructor
public class OperationHistoryService {
    private final OperationHistoryRepository operationHistoryRepository;
    private final OperationMapper operationMapper;

    @Transactional
    public void saveOperationHistory(OperationHistoryDto dto) {
        operationHistoryRepository.save(operationMapper.toOperationHistory(dto));
    }
}
