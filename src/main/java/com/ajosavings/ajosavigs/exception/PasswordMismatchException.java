package com.ajosavings.ajosavigs.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class PasswordMismatchException extends RuntimeException {
    private String message;
    private String status;
    public PasswordMismatchException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }
}

