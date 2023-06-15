package ru.lazarenko.warehouse.dto.login;

import lombok.*;
import ru.lazarenko.warehouse.dto.security.TokenDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String email;
    private List<String> roles;
    private TokenDto accessToken;
    private TokenDto refreshToken;
}
