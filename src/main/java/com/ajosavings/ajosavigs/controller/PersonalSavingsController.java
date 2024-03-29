package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.dto.request.TransactionRequest;
import com.ajosavings.ajosavigs.dto.response.SavingsPage;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.service.PersonalSavingsService;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/savings")
public class PersonalSavingsController {

    private final PersonalSavingsServiceImpl savingsService;
    @PostMapping("create")
    public ResponseEntity<PersonalSavings> createSaving(@RequestBody PersonalSavingsDto personalSavingsDto){
        try{
            return savingsService.createPersonalSavings(personalSavingsDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/withdraw/{savingsId}")
    public ResponseEntity<String> depositToGlobalWallet(@PathVariable Long savingsId, @RequestBody DepositDto depositDto) {
        try {
            return savingsService.withdrawToGlobalWallet(savingsId, depositDto.getAmount());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/{personalSavingsId}/add-money")
    public ResponseEntity<String> addMoneyToSavings(@PathVariable Long personalSavingsId, @RequestBody DepositDto depositDto) {
          return savingsService.addMoneyToSavings(personalSavingsId, depositDto.getAmount());
    }

    @GetMapping("/{savingId}/goal")
    public ResponseEntity<PersonalSavings> viewGoal(@PathVariable Long savingId) {
        try {
            PersonalSavings userGoal = savingsService.viewGoal(savingId);
            if (userGoal == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userGoal);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/explore-savings")
    public ResponseEntity<SavingsPage> getAllSavings(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();
        SavingsPage allSavings = savingsService.getAllSavings(users, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(allSavings);
    }
}
