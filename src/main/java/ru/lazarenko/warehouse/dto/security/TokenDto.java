package ru.lazarenko.warehouse.dto.security;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private LocalDateTime expirationDataTime;
}
