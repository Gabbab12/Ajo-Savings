package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface TransactionService {

//    ResponseEntity<?> withdraw(TransactionRequest transactionRequest);

    ResponseEntity<?> deposit(DepositDto depositDto);

    ResponseEntity<?> verifyDepositOtp(String transactionOtp, String username);

    ResponseEntity<?> verifyWithdrawalOtp(String transactionOtp, String username);

    ResponseEntity<String> withdraw(DepositDto depositDto);

    Page<TransactionHistory> getTransactionHistory(Users users, int page, int size);

    ResponseEntity<Double> getTotalAmountWithdrawn(Authentication authentication);
}
