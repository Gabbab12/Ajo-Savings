package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.LoginRequest;
import com.ajosavings.ajosavigs.dto.request.PasswordDTO;
import com.ajosavings.ajosavigs.dto.response.AuthenticationResponse;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.exception.UserNotFoundException;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.service.UsersService;
import com.ajosavings.ajosavigs.service.serviceImpl.UsersServiceImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private final UsersServiceImpl usersService;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String username) {
        try {
            PasswordToken passwordToken = usersService.forgotPassword(username);
            return ResponseEntity.ok("Password reset email sent successfully. Token: " + passwordToken.getToken());
        } catch (UserNotFoundException e) {
            log.error("User not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (MessagingException e) {
            log.error("Error sending reset email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending reset email: " + e.getMessage());
        }
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyPasswordToken(@PathVariable String token) {
        if (usersService.verifyPasswordToken(token)) {
            return ResponseEntity.ok("Password token is valid.");
        } else {
            return ResponseEntity.badRequest().body("Password token is invalid or expired.");
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody PasswordDTO passwordDTO) throws ResourceNotFoundException {
        return usersService.resetPassword(token, passwordDTO);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/registered-user")
    public ResponseEntity<AuthenticationResponse> loginRegisteredUser(@RequestBody LoginRequest request) {
        return usersService.loginRegisteredUser(request);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public void logout() {
        usersService.logout();
    }

}
