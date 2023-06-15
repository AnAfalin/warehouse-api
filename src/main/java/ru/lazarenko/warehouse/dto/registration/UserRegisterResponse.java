package ru.lazarenko.warehouse.dto.registration;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse {
    private String status;
    private String message;
}
