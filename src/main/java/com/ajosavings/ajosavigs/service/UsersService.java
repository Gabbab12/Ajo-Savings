package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.PasswordDTO;
import com.ajosavings.ajosavigs.dto.SignUpRequest;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UsersService {
   Users signUp(SignUpRequest signUpRequest);

    PasswordToken forgotPassword(String email) throws MessagingException;

    boolean verifyPasswordToken(String token);

    @Transactional
    ResponseEntity<String> resetPassword(String token, PasswordDTO passwordDTO) throws ResourceNotFoundException;
}

