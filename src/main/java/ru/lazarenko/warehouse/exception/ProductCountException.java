package ru.lazarenko.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductCountException extends RuntimeException {
    public ProductCountException(String message) {
        super(message);
    }
}
