package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.warehouse.dto.ReportDto;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportDto toReportDto(ManufactureAnalysis manufactureAnalysis);

    List<ReportDto> toReportDtoList(List<ManufactureAnalysis> data);

}
