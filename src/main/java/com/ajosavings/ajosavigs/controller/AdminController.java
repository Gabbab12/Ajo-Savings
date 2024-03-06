package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PersonalSavingsServiceImpl savingsService;
    private final AjoGroupServiceImpl ajoGroupService;
    private final TransactionServiceImpl transactionService;

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
}
