package com.ajosavings.ajosavigs.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSavingCreationRequest {

    private String savingTarget;
    private String savingTargetAmount;
    private String savingFrequency;
    private String startDate;
    private String withdrawalDate;
}
