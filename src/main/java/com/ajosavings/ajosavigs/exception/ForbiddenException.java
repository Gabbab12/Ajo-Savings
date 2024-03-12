package com.ajosavings.ajosavigs.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Setter
@Getter
public class ForbiddenException extends RuntimeException{

    private String message;
    private HttpStatus status;

    public ForbiddenException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
