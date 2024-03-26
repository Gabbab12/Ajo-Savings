package com.ajosavings.ajosavigs.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Data
public class BadRequestException extends RuntimeException {
    private String message;
    private String status;

    public BadRequestException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }

}
