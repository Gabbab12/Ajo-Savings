package com.ajosavings.ajosavigs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProjectSavingResponse {
    private Long id;
    private String savingTargetAmount;
    private String savingFrequency;
    private String startDate;
    private String withdrawalDate;

    public ProjectSavingResponse(Long id) {

    }
}
