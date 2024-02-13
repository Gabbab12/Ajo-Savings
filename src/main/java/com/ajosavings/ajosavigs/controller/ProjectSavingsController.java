package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.ProjectSavingCreationRequest;
import com.ajosavings.ajosavigs.dto.request.SavingGoalCreationRequest;
import com.ajosavings.ajosavigs.dto.response.ProjectSavingResponse;
import com.ajosavings.ajosavigs.models.SavingsGoal;
import com.ajosavings.ajosavigs.service.ProjectSavingService;
import com.ajosavings.ajosavigs.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project_saving")
public class ProjectSavingsController {

    private final ProjectSavingService projectSavingService;
    private final SavingGoalService savingGoalService;

    @PostMapping("/crate_saves")
    public ResponseEntity<ProjectSavingResponse> createSaving(@RequestBody  ProjectSavingCreationRequest projectSavingCreationRequest){
        return projectSavingService.createSaving(projectSavingCreationRequest);
    }
    @PostMapping("saving_goal")
    public ResponseEntity<SavingsGoal> addGoal(@RequestBody SavingGoalCreationRequest savingGoalCreationRequest){
        return savingGoalService.addGoal(savingGoalCreationRequest);
    }


    }
