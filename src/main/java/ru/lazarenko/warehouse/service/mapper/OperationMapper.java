package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.warehouse.dto.OperationHistoryDto;
import ru.lazarenko.warehouse.entity.OperationHistory;

@Mapper(componentModel = "spring")
public interface OperationMapper {
    OperationHistory toOperationHistory(OperationHistoryDto dto);
}
