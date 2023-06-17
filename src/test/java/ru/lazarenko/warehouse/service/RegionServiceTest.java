package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.entity.Region;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.repository.RegionRepository;
import ru.lazarenko.warehouse.service.mapper.RegionMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class RegionServiceTest {
    @Autowired
    RegionService underTest;

    @MockBean
    RegionRepository regionRepository;

    @MockBean
    RegionMapper regionMapper;

    RegionDto regionRequestDto1;
    RegionDto regionRequestDto2;
    Region region1;
    Region region2;
    RegionDto regionResponseDto1;
    RegionDto regionResponseDto2;

    @BeforeEach
    void prepare() {
        Storage storageRegion2 = Storage.builder()
                .id(1)
                .name("Sochi-str-1")
                .region(region2)
                .build();

        regionRequestDto1 = RegionDto.builder().name("Moscow").build();
        regionRequestDto2 = RegionDto.builder().name("Sochi").build();

        region1 = Region.builder().id(1).name("Moscow").storages(List.of()).build();
        region2 = Region.builder().id(2).name("Sochi").storages(List.of(storageRegion2)).build();

        regionResponseDto1 = RegionDto.builder().id(1).name("Moscow").build();
        regionResponseDto2 = RegionDto.builder().id(2).name("Sochi").build();
    }

    @Test
    @DisplayName("create region | NoUniqueObjectException | name is not unique")
    void createRegion_noUniqueObjectException_nameIsNotUnique() {
        doThrow(NoUniqueObjectException.class)
                .when(regionRepository)
                .findByName(anyString());

        assertThrows(NoUniqueObjectException.class, () -> underTest.createRegion(regionRequestDto1));
    }

    @Test
    @DisplayName("create region | successful created | name is unique")
    void createRegion_successfulCreated_nameIsNotUnique() {
        when(regionRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(regionMapper.toRegion(regionRequestDto1)).thenReturn(region1);

        when(regionRepository.save(any(Region.class)))
                .thenReturn(region1);

        ResponseDto result = underTest.createRegion(regionRequestDto1);

        verify(regionRepository, times(1))
                .save(any(Region.class));
        assertThat(result.getMessage()).isEqualTo("Region successful created: id='1'");
    }

    @Test
    @DisplayName("get all regions | result list is empty | regions do not exist")
    void getAllRegions_resultListIsEmpty_regionsDoNotExist() {
        when(regionRepository.findAll())
                .thenReturn(List.of());

        List<RegionDto> result = underTest.getAllRegions();
        verify(regionRepository, times(1))
                .findAll();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("get all regions | result list is not empty | regions exist")
    void getAllRegions_resultListIsNotEmpty_regionsExist() {
        List<Region> regions = List.of(region1, region2);
        List<RegionDto> regionDtos = List.of(regionResponseDto1, regionResponseDto2);

        when(regionRepository.findAll())
                .thenReturn(regions);

        when(regionMapper.toRegionDtoList(anyList()))
                .thenReturn(regionDtos);

        List<RegionDto> result = underTest.getAllRegions();
        verify(regionRepository, times(1))
                .findAll();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getId()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("Moscow"),
                () -> assertThat(result.get(1).getId()).isEqualTo(2),
                () -> assertThat(result.get(1).getName()).isEqualTo("Sochi")
        );
    }

    @Test
    @DisplayName("check exist and get region by name | NoFoundElementException | region does not exist")
    void checkExistAndGetRegionByName_noFoundElementException_regionDoesNotExist() {
        String name = "Moscow";

        doThrow(NoUniqueObjectException.class)
                .when(regionRepository)
                .findByName(anyString());

        assertThrows(NoUniqueObjectException.class, () -> underTest.checkExistAndGetRegionByName(name));
    }

    @Test
    @DisplayName("check exist and get region by name | correct returned object | region exist")
    void checkExistAndGetRegionByName_correctReturnedObject_regionExists() {
        String name = "Sochi";

        when(regionRepository.findByName(anyString()))
                .thenReturn(Optional.of(region2));

        Region result = underTest.checkExistAndGetRegionByName(name);

        verify(regionRepository, times(1))
                .findByName(anyString());

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(2),
                () -> assertThat(result.getName()).isEqualTo("Sochi")
        );
    }

    @Test
    @DisplayName("get region by id | optional not empty | region exists")
    void getRegionById_optionalNotEmpty_regionExists() {
        Integer id = 2;

        when(regionRepository.findById(anyInt()))
                .thenReturn(Optional.of(region2));

        Optional<Region> optionalResult = underTest.getRegionById(id);

        verify(regionRepository, times(1))
                .findById(anyInt());

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getId()).isEqualTo(2);
        assertThat(optionalResult.get().getName()).isEqualTo("Sochi");
    }

    @Test
    @DisplayName("get region by id | optional is empty | region does not exist")
    void getRegionById_optionalIsEmpty_regionExist() {
        Integer id = 10;

        when(regionRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Optional<Region> optionalResult = underTest.getRegionById(id);

        verify(regionRepository, times(1))
                .findById(anyInt());

        assertThat(optionalResult).isEmpty();
    }

    @Test
    @DisplayName("check unique name | NoUniqueObjectException | region name is not unique")
    void checkUniqueName_noUniqueObjectException_regionNameIsNotUnique() {
        String name = "Sochi";

        when(regionRepository.findByName(anyString()))
                .thenReturn(Optional.of(region2));

        assertThrows(NoUniqueObjectException.class, () -> underTest.checkUniqueName(name));
    }

    @Test
    @DisplayName("check unique name | optional is empty and no exception | region name is unique")
    void checkUniqueName_optionalIsEmptyAndNoException_regionNameIsUnique() {
        String name = "Krasnodar";

        when(regionRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        underTest.checkUniqueName(name);

        verify(regionRepository, times(1))
                .findByName(anyString());
    }

    @Test
    @DisplayName("get region with storage by name | optional is empty | region does not exist")
    void getRegionWithStoragesByName_optionalIsEmpty_regionExist() {
        String name = "Krasnodar";

        when(regionRepository.findWithStoragesByName(anyString()))
                .thenReturn(Optional.empty());

        Optional<Region> optionalResult = underTest.getRegionWithStoragesByName(name);

        verify(regionRepository, times(1))
                .findWithStoragesByName(anyString());

        assertThat(optionalResult).isEmpty();
    }

    @Test
    @DisplayName("""
            get region with storage by name
            | optional is not empty and region contains empty list storages
            | region exist, storage does not exist
            """)
    void getRegionWithStoragesByName_optionalIsEmptyAndEmptyListStorages_regionExistAndStorageDoesNotExist() {
        String name = "Moscow";

        when(regionRepository.findWithStoragesByName(anyString()))
                .thenReturn(Optional.of(region1));

        Optional<Region> optionalResult = underTest.getRegionWithStoragesByName(name);

        verify(regionRepository, times(1))
                .findWithStoragesByName(anyString());

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getStorages()).isEmpty();
    }

    @Test
    @DisplayName("""
            get region with storage by name
            | optional is not empty and region contains not empty list storages
            | region exist, storage does not exist
            """)
    void getRegionWithStoragesByName_optionalIsNotEmptyAndNotEmptyListStorages_regionExistAndStoragesExist() {
        String name = "Sochi";

        when(regionRepository.findWithStoragesByName(anyString()))
                .thenReturn(Optional.of(region2));

        Optional<Region> optionalResult = underTest.getRegionWithStoragesByName(name);

        verify(regionRepository, times(1))
                .findWithStoragesByName(anyString());

        assertThat(optionalResult).isNotEmpty();
        assertThat(optionalResult.get().getName()).isEqualTo("Sochi");
        assertThat(optionalResult.get().getStorages()).isNotEmpty();
        assertThat(optionalResult.get().getStorages().get(0).getName()).isEqualTo("Sochi-str-1");
    }
}