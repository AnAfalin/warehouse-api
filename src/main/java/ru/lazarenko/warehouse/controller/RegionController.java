package ru.lazarenko.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.dto.ResponseDto;
import ru.lazarenko.warehouse.service.RegionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    @PostMapping
    public ResponseDto addRegion(@RequestBody @Valid RegionDto request) {
        return regionService.createRegion(request);
    }

    @GetMapping
    public List<RegionDto> getAllRegions() {
        return regionService.getAllRegions();
    }

}
