package ru.lazarenko.warehouse.dto;

import lombok.*;
import ru.lazarenko.warehouse.entity.Role;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;

    private String username;

    private String password;

    private LocalDate registrationDate;

    private List<Role> roles;
}
