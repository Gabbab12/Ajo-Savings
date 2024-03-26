package com.ajosavings.ajosavigs.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiError {
    private HttpStatus status;
    private String errorMessage;
    private LocalDateTime time = LocalDateTime.now();
}
