package com.ajosavings.ajosavigs.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class IncorrectOldPasswordException extends RuntimeException {

    private String message;
    private HttpStatus status;
    public IncorrectOldPasswordException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
