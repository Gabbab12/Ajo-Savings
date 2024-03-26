package com.ajosavings.ajosavigs.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class NotFoundException extends RuntimeException{
    private String message;
    private HttpStatus status;

    public NotFoundException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
