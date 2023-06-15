package ru.lazarenko.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.dto.registration.UserRegisterResponse;
import ru.lazarenko.warehouse.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class UserController {
    private final UserService userService;

    @PostMapping("/api/reg")
    public UserRegisterResponse registrationUser(@Valid @RequestBody UserRegisterRequest request) {
        return userService.registerUser(request);
    }

}
