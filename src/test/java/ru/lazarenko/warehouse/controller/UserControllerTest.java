package ru.lazarenko.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.dto.registration.UserRegisterResponse;
import ru.lazarenko.warehouse.exception.UserEmailExistException;
import ru.lazarenko.warehouse.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class ValidationUserTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | all fields are incorrect")
        void validateUser_correctSizeValidationList_allFieldAreIncorrect() {
            UserRegisterRequest test = new UserRegisterRequest();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Username cannot be empty or null")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Password cannot be empty or null"))
            );
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | field 'username' is null")
        void validateUser_correctSizeValidationList_usernameIsNull() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .password("password123")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Username cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | field 'username' is empty")
        void validateUser_correctSizeValidationList_passwordIsEmpty() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .password("password123")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Username cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | field 'password' is empty")
        void validateUser_correctSizeValidationList_usernameIsEmpty() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("username")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Password cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | field 'password' is null")
        void validateUser_correctSizeValidationList_passwordIsNull() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("username")
                    .password("")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Password cannot be empty or null")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Password must contains 5-15 characters (uppercase letters, lowercase letters or numbers)"))
            );
        }

        @Test
        @DisplayName("validate user | size of validation list is 1 | field 'password' is incorrect")
        void validateUser_correctSizeValidationList_passwordIsIncorrect() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("username")
                    .password("1")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Password must contains 5-15 characters (uppercase letters, lowercase letters or numbers)",
                            validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate user | list validation is empty | obkect is correct")
        void validateUser_correctSizeValidationList_objectCorrect() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("username")
                    .password("password123")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertTrue(validationSet.isEmpty());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("registration user | status is ok | request is correct")
    void registrationUser_statusOk_requestIsCorrect() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("username")
                .password("password123")
                .build();

        UserRegisterResponse response = UserRegisterResponse.builder()
                .status(HttpStatus.CREATED.name())
                .message("User with login='1' successfully created")
                .build();

        when(userService.registerUser(any(UserRegisterRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/api/reg")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("User with login='1' successfully created"));
    }

    @Test
    @WithMockUser
    @DisplayName("registration user | status is not found | email is already exist")
    void registrationUser_statusNotFound_emailIsAlreadyExist() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("username")
                .password("password123")
                .build();

        doThrow(new UserEmailExistException("User with username = '%s' already exist".formatted(request.getUsername())))
                .when(userService)
                .registerUser(any(UserRegisterRequest.class));

        mvc.perform(post("/api/reg")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
}