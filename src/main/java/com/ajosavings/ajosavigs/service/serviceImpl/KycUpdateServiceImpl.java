package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.KycUpdateDTO;
import com.ajosavings.ajosavigs.models.KYCUpdates;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.KycUpdateRepository;
import com.ajosavings.ajosavigs.service.KycUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KycUpdateServiceImpl implements KycUpdateService {

    private final KycUpdateRepository kycUpdateRepository;

    @Override
    public KYCUpdates kycUpdates(KycUpdateDTO kycUpdateDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        Optional<KYCUpdates> kycUpdateExist = kycUpdateRepository.findByUsers(user);
        KYCUpdates kycUpdates = kycUpdateExist.orElseGet(KYCUpdates::new);

        kycUpdates.setGender(kycUpdateDTO.getGender());
        kycUpdates.setOccupation(kycUpdateDTO.getOccupation());
        kycUpdates.setDateOfBirth(kycUpdateDTO.getDateOfBirth());
        kycUpdates.setIdentificationType(kycUpdateDTO.getIdentificationType());
        kycUpdates.setBvn(kycUpdateDTO.getBvn());
        kycUpdates.setAddress(kycUpdateDTO.getAddress());
        kycUpdates.setIdentificationNumber(kycUpdateDTO.getIdentificationNumber());
        kycUpdates.setUploadIdentificationDocument(kycUpdateDTO.getUploadIdentificationDocument());
        kycUpdates.setUploadProofOfAddress(kycUpdateDTO.getUploadProofOfAddress());
        kycUpdates.setUsers(user);

        kycUpdateRepository.save(kycUpdates);
        return kycUpdates;
    }
}
