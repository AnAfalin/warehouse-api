package ru.lazarenko.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFoundElementException extends RuntimeException {
    public NoFoundElementException(String message) {
        super(message);
    }
}
