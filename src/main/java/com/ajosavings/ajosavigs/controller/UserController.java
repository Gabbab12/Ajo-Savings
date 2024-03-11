package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {
    private final UsersService usersService;

    @PatchMapping("/profile-picture")
    public ResponseEntity<String> updateProfilePicture(@RequestParam String profilePicture, @RequestParam String username) {
        return usersService.updateProfilePicture(profilePicture, username);
    }
}
