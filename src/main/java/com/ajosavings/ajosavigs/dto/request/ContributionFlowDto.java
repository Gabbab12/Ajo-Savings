package com.ajosavings.ajosavigs.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContributionFlowDto {
    private String name;
    private BigDecimal contributing;
    private BigDecimal withdrawing;
    private BigDecimal fee;
    private LocalDate nextCashOut;
    private LocalDate recentCashOut;
}
