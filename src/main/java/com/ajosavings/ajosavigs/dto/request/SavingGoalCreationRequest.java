package com.ajosavings.ajosavigs.dto.request;

import com.ajosavings.ajosavigs.models.ProjectSavings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingGoalCreationRequest {
    private String savingGoal;
    private ProjectSavings projectSavings;
}
