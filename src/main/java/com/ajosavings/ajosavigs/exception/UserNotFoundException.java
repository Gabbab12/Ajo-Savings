package com.ajosavings.ajosavigs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class UserNotFoundException extends RuntimeException{
    @ExceptionHandler
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        // Customize the response for user not found exception
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    public UserNotFoundException(String message){
        super(message);
    }
}
