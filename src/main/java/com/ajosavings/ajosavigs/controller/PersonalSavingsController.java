package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/savings")
public class PersonalSavingsController {

    private final PersonalSavingsServiceImpl savingsService;

    @PostMapping("create")
    public ResponseEntity<PersonalSavings> createSaving(@RequestBody PersonalSavingsDto personalSavingsDto){
        try{
            return savingsService.createPersonalSavings(personalSavingsDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
