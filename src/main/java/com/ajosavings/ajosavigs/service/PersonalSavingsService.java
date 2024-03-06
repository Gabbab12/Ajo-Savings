package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface PersonalSavingsService {
    ResponseEntity<PersonalSavings> createPersonalSavings(PersonalSavingsDto savingsDto);

    ResponseEntity<String> withdrawToGlobalWallet(Long savingsId, BigDecimal amount);

    void addMoneyToSavings(Long personalSavingsId, BigDecimal amount);

    PersonalSavings viewGoal(Long savingId);

    Page<PersonalSavings> getAllSavings(Users user, int page, int size);

    ResponseEntity<Double> getTotalAmountSaved(Authentication authentication);
}
