package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.SignUpRequest;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.PasswordTokenRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/signup")
@RequiredArgsConstructor
    public class SignUpController {

        private final UsersService usersService;
        private final UserRepository userRepository;
        private final PasswordTokenRepository passwordTokenRepository;

    @PostMapping("/normal")
    public ResponseEntity<String> signupNormal(@RequestBody SignUpRequest signUpRequest) {
        try {
            usersService.signUp(signUpRequest);
            return ResponseEntity.ok("signup successful, please proceed to your email for the verification of your account");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during signup: + e.getMassage");
        }

    }
    @GetMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        PasswordToken passwordToken = passwordTokenRepository.findByUsernameAndVerificationToken(username, otp);
        if (passwordToken != null && passwordToken.getIsValid() && !passwordToken.getExpirationTime().isBefore(LocalDateTime.now().plusMinutes(5))) {
            passwordToken.setIsValid(false);
            passwordTokenRepository.save(passwordToken);
            return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
        }
    }

}



