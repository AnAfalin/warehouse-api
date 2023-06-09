package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.lazarenko.warehouse.dto.StorageDto;
import ru.lazarenko.warehouse.entity.Storage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StorageMapper {
    Storage toStorage(StorageDto dto);

    StorageDto toStorageDto(Storage storage);

    List<StorageDto> toStorageDtoList(List<Storage> storages);

    void update(@MappingTarget Storage storage, StorageDto storageDto);

}
