package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.dto.ResponseDto;
import ru.lazarenko.warehouse.entity.Region;
import ru.lazarenko.warehouse.exception.NoUniqueObjectException;
import ru.lazarenko.warehouse.repository.RegionRepository;
import ru.lazarenko.warehouse.service.mapper.RegionMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Transactional
    public ResponseDto createRegion(RegionDto request) {
        checkUniqueName(request.getName());

        Region region = regionMapper.toRegion(request);
        Region savedRegion = regionRepository.save(region);

        log.error("Region successful created: {}", savedRegion);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.toString())
                .message("Region successful created: id='%s'".formatted(savedRegion.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<RegionDto> getAllRegions() {
        List<Region> regions = regionRepository.findAll();
        return regionMapper.toRegionDtoList(regions);
    }

    @Transactional(readOnly = true)
    public Region checkExistAndGetRegionByName(String name) {
        return regionRepository.findByName(name)
                .orElseThrow(() -> new NoUniqueObjectException("Region with name='%s' already exist".formatted(name)));
    }

    @Transactional(readOnly = true)
    public Optional<Region> getById(Integer id) {
        return regionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public void checkUniqueName(String name) {
        Optional<Region> foundRegion = regionRepository.findByName(name);
        if (foundRegion.isPresent()) {
            log.error("Region with name='{}' already exist", name);
            throw new NoUniqueObjectException("Region with name='%s' already exist".formatted(name));
        }
    }

    @Transactional(readOnly = true)
    public Optional<Region> getWithStoragesByName(String name) {
        return regionRepository.findWithStoragesByName(name);
    }

}
