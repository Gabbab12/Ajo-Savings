package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.PersonalSavingsRepository;
import com.ajosavings.ajosavigs.service.PersonalSavingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalSavingsServiceImpl implements PersonalSavingsService {

    private final PersonalSavingsRepository personalSavingsRepository;

    @Override
    public ResponseEntity<PersonalSavings> createPersonalSavings(PersonalSavingsDto savingsDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        PersonalSavings personalSavings = mapToEntity(savingsDto);
        personalSavings.setUsers(user);

        long daysLeft = calculateDaysLeft(savingsDto.getStartDate(), savingsDto.getWithdrawalDate());
        personalSavings.setDaysLeft(daysLeft);

        personalSavingsRepository.save(personalSavings);
        log.info("Savings successfully created");
        log.info(String.valueOf(personalSavings));
        return ResponseEntity.status(HttpStatus.CREATED).body(personalSavings);

    }

    public PersonalSavings mapToEntity(PersonalSavingsDto savingsDto){
        PersonalSavings personalSavings = new PersonalSavings();
        personalSavings.setTarget(savingsDto.getTarget());
        personalSavings.setTargetAmount(savingsDto.getTargetAmount());
        personalSavings.setFrequency(savingsDto.getFrequency());
        personalSavings.setStartDate(savingsDto.getStartDate());
        personalSavings.setWithdrawalDate(savingsDto.getWithdrawalDate());

        return personalSavings;
    }

    private long calculateDaysLeft(LocalDate startDate, LocalDate withdrawalDate){
        long enlapsedDate = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        long totalDays = ChronoUnit.DAYS.between(startDate, withdrawalDate);
        long daysLeft = totalDays - enlapsedDate;

        return Math.max(0, daysLeft);
    }
}
