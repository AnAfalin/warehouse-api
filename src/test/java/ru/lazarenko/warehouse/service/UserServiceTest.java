package ru.lazarenko.warehouse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.dto.registration.UserRegisterResponse;
import ru.lazarenko.warehouse.entity.User;
import ru.lazarenko.warehouse.exception.NoFoundElementException;
import ru.lazarenko.warehouse.exception.UserUsernameExistException;
import ru.lazarenko.warehouse.repository.UserRepository;
import ru.lazarenko.warehouse.service.mapper.UserMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService underTest;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserMapper userMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("register user | UserUsernameExistException | user with 'username' already exists")
    void registerUser_userUsernameExistException_usernameAlreadyExist() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("user")
                .password("password")
                .build();
        User user = User.builder()
                .username("user")
                .password("VjFSQ2ExSXlWblJVV0hCaFUwWndjVmxzV2taUFVUMDk=")
                .build();

        when(passwordEncoder.encode(anyString()))
                .thenReturn("password");

        when(userMapper.toUser(any(UserRegisterRequest.class)))
                .thenReturn(user);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        assertThrows(UserUsernameExistException.class, () -> underTest.registerUser(request));
    }

    @Test
    @DisplayName("register user | successfully register | user with 'username' does not exist")
    void registerUser_successfullyRegister_usernameUnique() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("user")
                .password("password")
                .build();
        User user = User.builder()
                .username("user")
                .password("VjFSQ2ExSXlWblJVV0hCaFUwWndjVmxzV2taUFVUMDk=")
                .build();

        when(userMapper.toUser(any(UserRegisterRequest.class)))
                .thenReturn(user);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        user.setId(1);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserRegisterResponse result = underTest.registerUser(request);

        verify(userRepository, times(1))
                .save(user);

        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getMessage()).isEqualTo("User with login='user' successfully created");
    }

    @Test
    @DisplayName("find user by id | NoFoundElementException | user does not exist")
    void findUserById_noFoundElementException_userDoesNotExist() {
        Integer id = 100;
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.findUserById(id));
    }

    @Test
    @DisplayName("find user by id | returned correct user | user exists")
    void findUserById_returnedUser_userExists() {
        Integer id = 1;
        User user = User.builder()
                .id(1)
                .username("user")
                .password("VjFSQ2ExSXlWblJVV0hCaFUwWndjVmxzV2taUFVUMDk=")
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        User result = underTest.findUserById(id);

        verify(userRepository, times(1))
                .findById(anyInt());

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("user");
    }
}