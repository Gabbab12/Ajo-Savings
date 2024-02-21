package com.ajosavings.ajosavigs.dto.request;

import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class PersonalSavingsDto {
    private String target;
    private BigDecimal targetAmount;
    private PaymentPeriod frequency;
    private LocalDate startDate;
    private LocalDate withdrawalDate;
}
