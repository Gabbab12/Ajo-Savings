package com.ajosavings.ajosavigs.controller;
import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.TransactionRequest;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.service.serviceImpl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transaction")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping("/add-money")
    public ResponseEntity<?> addMoneyToWallet(@RequestBody DepositDto depositDto) {
        return transactionService.deposit(depositDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp, String username) {
        return transactionService.verifyOtp(otp, username);
    }

    @GetMapping("transaction-history")
    public ResponseEntity<Page<TransactionHistory>> getTransactionHistory(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();
        Page<TransactionHistory> transactionHistoryPage = transactionService.getTransactionHistory(user, page, size);
        return ResponseEntity.ok(transactionHistoryPage);
    }

//    @PostMapping("withdraw")
//    public ResponseEntity<?> withdrawal(@RequestBody TransactionRequest transactionRequest){
//        return transactionService.withdraw(transactionRequest);
//    }
}
