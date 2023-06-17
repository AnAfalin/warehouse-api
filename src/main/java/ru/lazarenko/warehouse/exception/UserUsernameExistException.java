package ru.lazarenko.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserUsernameExistException extends RuntimeException {
    public UserUsernameExistException(String message) {
        super(message);
    }
}
