package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.service.AjoGroupService;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.TransactionServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.UsersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PersonalSavingsServiceImpl savingsService;
    private final AjoGroupServiceImpl ajoGroupService;
    private final TransactionServiceImpl transactionService;
    private final UsersServiceImpl usersService;

    @GetMapping("/total-amount-saved")
    public ResponseEntity<Double> getTotalAmountSaved(Authentication authentication){
        return savingsService.getTotalAmountSaved(authentication);
    }
    @GetMapping("/total-saved-groups")
    public ResponseEntity<Long> getTotalSavingGroups() {
        return ajoGroupService.getTotalSavingGroups();
    }

    @GetMapping("/total-amount-withdrawn")
    public ResponseEntity<Double> getTotalAmountWithdrawn(Authentication authentication) {
        return transactionService.getTotalAmountWithdrawn(authentication);
    }
    @GetMapping("/total-contributions")
    public ResponseEntity<Double> getTotalContributions(Authentication authentication){
        return ajoGroupService.getTotalContributions(authentication);
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<Long> getAllUsersNumber(Authentication authentication){
        return usersService.getAllUsers(authentication);
    }

    @GetMapping("/total-ajo-groups")
    public ResponseEntity<Long> getTotalAjoGroups(Authentication authentication) {
        return ajoGroupService.getTotalAjoGroups(authentication);

    }
}
