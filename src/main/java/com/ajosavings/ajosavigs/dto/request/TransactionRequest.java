package com.ajosavings.ajosavigs.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private BigDecimal amount;
    private String email;
}
