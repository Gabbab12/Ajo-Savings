package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.KycUpdateDTO;
import com.ajosavings.ajosavigs.models.KYCUpdates;

public interface KycUpdateService {
    KYCUpdates kycUpdates(KycUpdateDTO kycUpdateDTO);
}
