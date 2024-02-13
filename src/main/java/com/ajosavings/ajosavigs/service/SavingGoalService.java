package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.SavingGoalCreationRequest;
import com.ajosavings.ajosavigs.models.SavingsGoal;
import org.springframework.http.ResponseEntity;

public interface SavingGoalService {
    ResponseEntity<SavingsGoal> addGoal(SavingGoalCreationRequest savingGoalCreationRequest);
}
