package com.ajosavings.ajosavigs.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Data
public class UserNotFoundException extends RuntimeException{
    private  String message;
    private String status;
    public UserNotFoundException(String message, HttpStatus status){
        this.message = message;
        this.status = String.valueOf(status);
    }
}
