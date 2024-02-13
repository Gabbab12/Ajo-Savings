package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.ProjectSavingCreationRequest;
import com.ajosavings.ajosavigs.dto.response.ProjectSavingResponse;
import com.ajosavings.ajosavigs.models.ProjectSavings;
import com.ajosavings.ajosavigs.models.SavingsGoal;
import com.ajosavings.ajosavigs.repository.ProjectSavingRepository;
import com.ajosavings.ajosavigs.service.ProjectSavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class ProjectSavingImpl implements ProjectSavingService {

    private final ProjectSavingRepository projectSavingRepository;

    @Override
    public ResponseEntity<ProjectSavingResponse> createSaving(ProjectSavingCreationRequest projectSavingCreationRequest) {
        try {
            ProjectSavings newSavings = new ProjectSavings();
            newSavings.setSavingTarget(projectSavingCreationRequest.getSavingTarget());
            newSavings.setSavingTargetAmount(new BigDecimal(projectSavingCreationRequest.getSavingTargetAmount()));
            newSavings.setSavingFrequency(Integer.valueOf(projectSavingCreationRequest.getSavingFrequency()));
            newSavings.setStartDate(LocalDateTime.parse(projectSavingCreationRequest.getStartDate()));
            newSavings.setWithdrawalDate(LocalDateTime.parse(projectSavingCreationRequest.getWithdrawalDate()));
            newSavings.setSavingsGoal(new SavingsGoal());

            ProjectSavings savedSavings = projectSavingRepository.save(newSavings);

            ProjectSavingResponse savedSavingResponse = new ProjectSavingResponse(newSavings.getId());

            return new ResponseEntity<>(savedSavingResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @Override
//    public ResponseEntity<ProjectSavingResponse> createSaving(ProjectSavingCreationRequest projectSavingCreationRequest) {
//        try {
//            ProjectSavings newSavings = new ProjectSavings();
//            newSavings.setSavingTarget(projectSavingCreationRequest.getSavingTarget());
//            newSavings.setSavingTargetAmount(new BigDecimal(projectSavingCreationRequest.getSavingTargetAmount()));
//            newSavings.setSavingFrequency(Integer.parseInt(projectSavingCreationRequest.getSavingFrequency()));
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            newSavings.setStartDate(LocalDateTime.parse(projectSavingCreationRequest.getStartDate(), formatter));
//            newSavings.setWithdrawalDate(LocalDateTime.parse(projectSavingCreationRequest.getWithdrawalDate(), formatter));
//
//            // SavingsGoal is another entity and needs to be associated properly
//            SavingsGoal savingsGoal = new SavingsGoal();
//            newSavings.setSavingsGoal(savingsGoal);
//
//            ProjectSavings savedSaving = projectSavingRepository.save(newSavings);
//
//            ProjectSavingResponse response = new ProjectSavingResponse(savedSaving.getId());
//
//            return new ResponseEntity<>(response, HttpStatus.CREATED);
//        } catch (DateTimeParseException | NumberFormatException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
