package ru.lazarenko.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailExistException extends RuntimeException {
    public UserEmailExistException(String message) {
        super(message);
    }
}
