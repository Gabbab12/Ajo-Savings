package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.KycUpdateDTO;
import com.ajosavings.ajosavigs.models.KYCUpdates;
import com.ajosavings.ajosavigs.service.serviceImpl.KycUpdateServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/update")
public class KycUpdatesController {

    private final KycUpdateServiceImpl kycUpdateService;

    @PostMapping("/kyc-update")
    public ResponseEntity<String> kycUpate(@RequestBody KycUpdateDTO kycUpdateDTO) throws IOException {
        kycUpdateService.kycUpdates(kycUpdateDTO);
        return new ResponseEntity<>("KYC update successful", HttpStatus.CREATED);
    }

    @GetMapping("/getKyc")
    public ResponseEntity<String> getKyc() {
        return kycUpdateService.getKyc();
    }
}
