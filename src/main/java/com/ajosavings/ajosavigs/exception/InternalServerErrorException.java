package com.ajosavings.ajosavigs.exception;


import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class InternalServerErrorException extends Throwable {
    @ExceptionHandler
    public ResponseEntity<String> handleMessagingException(MessagingException ex) {
        // Customize the response for messaging exception (SMTP error)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending reset email: " + ex.getMessage());
    }
    public InternalServerErrorException(String s, HttpStatus httpStatus) {
    }
}

