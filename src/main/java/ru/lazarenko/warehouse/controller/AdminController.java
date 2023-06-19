package ru.lazarenko.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lazarenko.warehouse.dto.ReportDto;
import ru.lazarenko.warehouse.dto.UserDto;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;
import ru.lazarenko.warehouse.scheduled.ManufactureAnalysisService;
import ru.lazarenko.warehouse.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final ManufactureAnalysisService manufactureAnalysisService;

    @GetMapping("/users")
    private List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/report")
    private List<ReportDto> getReport() {
        return manufactureAnalysisService.getReport();
    }
}
