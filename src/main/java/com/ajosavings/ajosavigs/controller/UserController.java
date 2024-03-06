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

    @GetMapping("/new-registrations")
    public ResponseEntity<Integer> getTotalNewRegistrations(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        int totalNewRegistrations = usersService.getTotalNewRegistrations(startDate, endDate);
        return ResponseEntity.ok(totalNewRegistrations);
    }
}
