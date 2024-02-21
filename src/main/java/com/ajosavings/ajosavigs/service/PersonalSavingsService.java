package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import org.springframework.http.ResponseEntity;

public interface PersonalSavingsService {
    ResponseEntity<PersonalSavings> createPersonalSavings(PersonalSavingsDto savingsDto);
}
