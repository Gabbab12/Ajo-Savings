package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.ProjectSavingCreationRequest;
import com.ajosavings.ajosavigs.dto.response.ProjectSavingResponse;
import org.springframework.http.ResponseEntity;

public interface ProjectSavingService {
    ResponseEntity<ProjectSavingResponse> createSaving(ProjectSavingCreationRequest projectSavingCreationRequest);
}
