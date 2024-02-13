package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.SavingGoalCreationRequest;
import com.ajosavings.ajosavigs.models.ProjectSavings;
import com.ajosavings.ajosavigs.models.SavingsGoal;
import com.ajosavings.ajosavigs.repository.SavingGoalRepository;
import com.ajosavings.ajosavigs.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavinGoalImpl implements SavingGoalService {

    private final SavingGoalRepository savingGoalRepository;

    @Override
    public ResponseEntity<SavingsGoal> addGoal(SavingGoalCreationRequest savingGoalCreationRequest){
        SavingsGoal addGoal = new SavingsGoal();
        addGoal.setSavingGoal(savingGoalCreationRequest.getSavingGoal());
        addGoal.setProjectSavings(new ProjectSavings());
        try {
            SavingsGoal newGoal = savingGoalRepository.save(addGoal);
            return new ResponseEntity<>(newGoal, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
