package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.dto.response.SavingsWalletDTO;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.service.PersonalSavingsService;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/savings")
public class PersonalSavingsController {

    private final PersonalSavingsServiceImpl savingsService;
    private final PersonalSavingsService personalSavingsService;

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
        try {
            personalSavingsService.addMoneyToSavings(personalSavingsId, depositDto.getAmount());
            return ResponseEntity.ok("Money successfully added to savings wallet.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add money to savings wallet.");
        }
    }

    @GetMapping("/{savingId}/goal")
    public ResponseEntity<PersonalSavings> viewGoal(@PathVariable Long savingId) {
        try {
            PersonalSavings userGoal = personalSavingsService.viewGoal(savingId);
            if (userGoal == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userGoal);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/view/{savingsId}")
    public ResponseEntity<BigDecimal> viewSavingsWallet(@PathVariable Long savingsId) {
        BigDecimal amountSaved = personalSavingsService.viewSavingsWallet(savingsId).getAmountSaved();
        return ResponseEntity.ok(amountSaved);
    }
}
