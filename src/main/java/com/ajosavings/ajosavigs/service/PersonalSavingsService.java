package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface PersonalSavingsService {
    ResponseEntity<PersonalSavings> createPersonalSavings(PersonalSavingsDto savingsDto);

    ResponseEntity<String> withdrawToGlobalWallet(Long savingsId, BigDecimal amount);

    void addMoneyToSavings(Long savingsId, BigDecimal amount);
}
