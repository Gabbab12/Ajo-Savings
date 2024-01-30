package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.KycUpdateDTO;
import com.ajosavings.ajosavigs.models.KYCUpdates;
import com.ajosavings.ajosavigs.service.serviceImpl.KycUpdateServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/update")
public class KycUpdatesController {

    private final KycUpdateServiceImpl kycUpdateService;

    @PostMapping("/kycUpdate")
    public ResponseEntity<String> kycUpate(@RequestBody KycUpdateDTO kycUpdateDTO){
        kycUpdateService.kycUpdates(kycUpdateDTO);
        return new ResponseEntity<>("KYC update successful", HttpStatus.CREATED);
    }
}
