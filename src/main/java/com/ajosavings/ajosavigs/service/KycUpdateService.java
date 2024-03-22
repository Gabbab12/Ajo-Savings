package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.KycUpdateDTO;
import com.ajosavings.ajosavigs.models.KYCUpdates;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface KycUpdateService {
    KYCUpdates kycUpdates(KycUpdateDTO kycUpdateDTO) throws IOException;
    ResponseEntity<String> getKyc();
}
