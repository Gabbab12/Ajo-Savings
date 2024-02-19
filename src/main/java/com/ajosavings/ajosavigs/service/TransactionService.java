package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.TransactionRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface TransactionService {

//    ResponseEntity<?> withdraw(TransactionRequest transactionRequest);

    ResponseEntity<?> deposit(DepositDto depositDto);

    ResponseEntity<?> verifyOtp(String transactionOtp, String username);
}
