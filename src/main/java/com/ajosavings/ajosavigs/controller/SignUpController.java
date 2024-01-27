package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.SignUpRequest;
import com.ajosavings.ajosavigs.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signup")
    public class SignUpController {


        private final UsersService usersService;

    public SignUpController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/normal")
    public ResponseEntity<String> signupNormal(@RequestBody SignUpRequest signUpRequest) {
        try {
            usersService.signUp(signUpRequest);
            return ResponseEntity.ok("signup successful, please proceed to your email for the verification of your account");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during signup: + e.getMassage");
        }

    }

}



