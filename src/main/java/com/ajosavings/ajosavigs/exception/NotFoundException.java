package com.ajosavings.ajosavigs.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class NotFoundException extends RuntimeException{
    private String message;
    private HttpStatus status;

    public NotFoundException(String message, String message1, String status) {
        super(message);
        this.message = message1;
    }

    public NotFoundException(String ajoGroupNotFoundWithId, HttpStatus httpStatus) {
    }
}
