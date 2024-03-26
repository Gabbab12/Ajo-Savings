package com.ajosavings.ajosavigs.exception;

import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
@Data
public class ResourceNotFoundException extends RuntimeException {
    private String message;
    private String status;
    public ResourceNotFoundException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }
}
