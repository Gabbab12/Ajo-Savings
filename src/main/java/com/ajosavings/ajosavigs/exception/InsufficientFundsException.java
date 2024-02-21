package com.ajosavings.ajosavigs.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends RuntimeException{
    private String message;
    private String status;

    public InsufficientFundsException(String message, HttpStatus status){
        this.message = message;
        this.status = String.valueOf(status);
    }
}
