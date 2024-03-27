package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.LoginRequest;
import com.ajosavings.ajosavigs.dto.request.PasswordChangeDTO;
import com.ajosavings.ajosavigs.dto.request.PasswordDTO;
import com.ajosavings.ajosavigs.dto.request.ProfileUpdateDto;
import com.ajosavings.ajosavigs.dto.response.AuthenticationResponse;
import com.ajosavings.ajosavigs.exception.BadRequestException;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.exception.UserNotFoundException;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
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

    private final UsersService usersService;

    @PostMapping("/forgot")
    public ResponseEntity<PasswordToken> forgotPassword(@RequestParam String username) throws MessagingException {
       return usersService.forgotPassword(username);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyPasswordToken(@PathVariable String token) {
        if (usersService.verifyPasswordToken(token)) {
            return ResponseEntity.ok("You can proceed to resetting your email");
        } else {
            throw new BadRequestException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody PasswordDTO passwordDTO) {
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

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO){
        return  usersService.changePassword(passwordChangeDTO);
    }

    @GetMapping("/users")
    public ResponseEntity<Users> getUser(@RequestParam String email) {
        return usersService.getUser(email);
    }

    @PutMapping("profile-update")
    public ResponseEntity<String> profileUpdate(@RequestBody ProfileUpdateDto profileUpdateDto){
       return usersService.updateUserDetails(profileUpdateDto);
    }
}
