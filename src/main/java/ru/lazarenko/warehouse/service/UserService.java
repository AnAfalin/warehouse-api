package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.dto.registration.UserRegisterResponse;
import ru.lazarenko.warehouse.entity.Role;
import ru.lazarenko.warehouse.entity.User;
import ru.lazarenko.warehouse.exception.NoSuchUserException;
import ru.lazarenko.warehouse.exception.UserEmailExistException;
import ru.lazarenko.warehouse.model.UserRole;
import ru.lazarenko.warehouse.repository.UserRepository;
import ru.lazarenko.warehouse.security.JwtService;
import ru.lazarenko.warehouse.service.mapper.UserMapper;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = userMapper.toUser(request);

        if (isExistUsername(request.getUsername())) {
            throw new UserEmailExistException("User with username = '%s' already exist");
        }

        Role role = new Role();
        role.setName(UserRole.USER);
        newUser.setRoles(List.of(role));

        userRepository.save(newUser);

        return UserRegisterResponse.builder()
                .message("User with login='%s' successfully created".formatted(newUser.getUsername()))
                .status(HttpStatus.CREATED.name())
                .build();
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id='%s' not found".formatted(id)));
    }

    private boolean isExistUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}