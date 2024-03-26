package com.ajosavings.ajosavigs.exception;


import jakarta.mail.MessagingException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Data
public class InternalServerErrorException extends Throwable {
   private String message;
   private String status;
    public InternalServerErrorException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }
}

