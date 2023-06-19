package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.dto.UserDto;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.dto.registration.UserRegisterResponse;
import ru.lazarenko.warehouse.entity.Role;
import ru.lazarenko.warehouse.entity.User;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.exception.NoSuchUserException;
import ru.lazarenko.warehouse.exception.UserUsernameExistException;
import ru.lazarenko.warehouse.model.UserRole;
import ru.lazarenko.warehouse.repository.UserRepository;
import ru.lazarenko.warehouse.service.mapper.UserMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = userMapper.toUser(request);

        if (isExistUsername(request.getUsername())) {
            throw new UserUsernameExistException("User with username = '%s' already exist");
        }

        Role role = new Role();
        role.setName(UserRole.MANAGER);
        newUser.setRoles(List.of(role));

        User savedUser = userRepository.save(newUser);
        log.info("User successful created: {}", savedUser);

        return UserRegisterResponse.builder()
                .message("User with login='%s' successfully created".formatted(newUser.getUsername()))
                .status(HttpStatus.CREATED.name())
                .build();
    }

    @Transactional(readOnly = true)
    public User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("User with id='%s' not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAllWithRoles();
        return userMapper.toUserDtoList(users);
    }

    private boolean isExistUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
