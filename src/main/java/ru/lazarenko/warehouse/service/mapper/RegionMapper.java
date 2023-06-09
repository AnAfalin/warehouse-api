package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.entity.Region;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    Region toRegion(RegionDto dto);

    RegionDto toRegionDto(Region region);

    List<RegionDto> toRegionDtoList(List<Region> regions);

}
