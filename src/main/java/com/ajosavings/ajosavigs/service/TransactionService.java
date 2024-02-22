package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.TransactionRequest;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface TransactionService {

//    ResponseEntity<?> withdraw(TransactionRequest transactionRequest);

    ResponseEntity<?> deposit(DepositDto depositDto);

    ResponseEntity<?> verifyOtp(String transactionOtp, String username);

    Page<TransactionHistory> getTransactionHistory(Users users, int page, int size);
}
