package ru.lazarenko.warehouse.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    @NotBlank(message = "Username cannot be empty or null")
    private String username;

    @NotBlank(message = "Password cannot be empty or null")
    private String password;
}