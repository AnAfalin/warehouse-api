package ru.lazarenko.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.lazarenko.warehouse.dto.ReportDto;
import ru.lazarenko.warehouse.dto.UserDto;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.dto.storage.StorageDto;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Role;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.ChangeType;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.model.UserRole;
import ru.lazarenko.warehouse.scheduled.ManufactureAnalysisService;
import ru.lazarenko.warehouse.service.UserService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    ManufactureAnalysisService manufactureAnalysisService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("get all users | status is ok and result list is empty | users don't exist")
    void getAllUsersCategories_statusOkAndEmptyResultList_usersDoNotExist() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/admin/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get all users | status is ok and result list is not empty | users exist")
    void getAllUsers_statusOkAndNotEmptyResultList_usersExist() throws Exception {
        Role roleAdmin = new Role();
        roleAdmin.setId(1);
        roleAdmin.setName(UserRole.ADMIN);

        Role roleManager = new Role();
        roleManager.setId(2);
        roleManager.setName(UserRole.MANAGER);

        UserDto userDto = UserDto.builder()
                .id(1)
                .username("admin")
                .password("VjFSQ2ExSXlWblJVV0hCaFUwWndjVmxzV2taUFVUMDk=")
                .roles(List.of(roleAdmin, roleManager))
                .build();

        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(MockMvcRequestBuilders.get("/api/admin/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].roles.size()").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("get report | status is ok and result list is empty | notices don't exist")
    void getReport_statusOkAndEmptyResultList_noticesDoNotExist() throws Exception {
        when(manufactureAnalysisService.getReport())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/admin/report"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get report | status is ok and result list is not empty | notices exist")
    void getReport_statusOkAndNotEmptyResultList_noticesExist() throws Exception {
        StorageDto storage = StorageDto.builder().id(1).name("str-1").build();
        ProductDto product = ProductDto.builder().id(1).price(new BigDecimal(100)).name("Coffee").build();

        ReportDto reportDto = ReportDto.builder()
                .id(11)
                .storage(storage)
                .product(product)
                .operation(OperationType.LOADING)
                .changeType(ChangeType.DECREASE)
                .build();

        when(manufactureAnalysisService.getReport())
                .thenReturn(List.of(reportDto));

        mvc.perform(MockMvcRequestBuilders.get("/api/admin/report"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].storage.name").value("str-1"))
                .andExpect(jsonPath("$[0].product.name").value("Coffee"))
                .andExpect(jsonPath("$.size()").value(1));
    }
}