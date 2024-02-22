package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.PersonalSavingsDto;
import com.ajosavings.ajosavigs.enums.TransactionType;
import com.ajosavings.ajosavigs.exception.AccessDeniedException;
import com.ajosavings.ajosavigs.exception.InsufficientFundsException;
import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.PersonalSavingsRepository;
import com.ajosavings.ajosavigs.repository.TransactionHistoryRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.PersonalSavingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalSavingsServiceImpl implements PersonalSavingsService {

    private final PersonalSavingsRepository personalSavingsRepository;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public ResponseEntity<PersonalSavings> createPersonalSavings(PersonalSavingsDto savingsDto) {
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

    public PersonalSavings mapToEntity(PersonalSavingsDto savingsDto) {
        PersonalSavings personalSavings = new PersonalSavings();
        personalSavings.setTarget(savingsDto.getTarget());
        personalSavings.setTargetAmount(savingsDto.getTargetAmount());
        personalSavings.setFrequency(savingsDto.getFrequency());
        personalSavings.setStartDate(savingsDto.getStartDate());
        personalSavings.setWithdrawalDate(savingsDto.getWithdrawalDate());

        return personalSavings;
    }

    private long calculateDaysLeft(LocalDate startDate, LocalDate withdrawalDate) {
        long enlapsedDate = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        long totalDays = ChronoUnit.DAYS.between(startDate, withdrawalDate);
        long daysLeft = totalDays - enlapsedDate;

        return Math.max(0, daysLeft);
    }

    @Override
    public ResponseEntity<String> withdrawToGlobalWallet(Long savingsId, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();

        Optional<PersonalSavings> optionalPersonalSavings = personalSavingsRepository.findById(savingsId);
        if (optionalPersonalSavings.isEmpty()) {
            throw new ResourceNotFoundException("Savings not found with ID: " + savingsId);
        }

        try {
            PersonalSavings personalSavings = getPersonalSavings(amount, optionalPersonalSavings, users);
            personalSavingsRepository.save(personalSavings);

            BigDecimal updatedGlobalWallet = users.getGlobalWallet().add(amount);
            users.setGlobalWallet(updatedGlobalWallet);
            userRepository.save(users);

            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setAmount(amount);
            transactionHistory.setDate(LocalDate.now());
            transactionHistory.setName(personalSavings.getTarget());
            transactionHistory.setType(TransactionType.CREDIT);
            transactionHistory.setUser(users);
            transactionHistoryRepository.save(transactionHistory);

            return ResponseEntity.accepted().body("Successfully transferred money to your global wallet");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    private PersonalSavings getPersonalSavings(BigDecimal amount, Optional<PersonalSavings> optionalPersonalSavings, Users users) {
        PersonalSavings personalSavings = optionalPersonalSavings.get();

        if (!personalSavings.getUsers().equals(users)) {
            throw new AccessDeniedException("You are not authorized to withdraw money from this wallet", HttpStatus.UNAUTHORIZED);
        }
        if (amount.compareTo(personalSavings.getAmountSaved()) > 0) {
            log.error("insufficient fund");
            throw new InsufficientFundsException("You do not have sufficient balance in this savings", HttpStatus.NOT_ACCEPTABLE);
        }
        BigDecimal updatedAmountSaved = personalSavings.getAmountSaved().subtract(amount);
        personalSavings.setAmountSaved(updatedAmountSaved);
        return personalSavings;
    }

    @Override
    public void addMoneyToSavings(Long personalSavingsId, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();

        Optional<PersonalSavings> optionalPersonalSavings = personalSavingsRepository.findById(personalSavingsId);
        if (optionalPersonalSavings.isEmpty()) {
            throw new ResourceNotFoundException("Savings not found with ID: " + personalSavingsId);
        }

        try {
            PersonalSavings personalSavings = optionalPersonalSavings.get();
            if (!personalSavings.getUsers().equals(users)) {
                throw new AccessDeniedException("You are not authorized to add money to this savings", HttpStatus.UNAUTHORIZED);
            }

            BigDecimal updatedAmountSaved = personalSavings.getAmountSaved().add(amount);
            personalSavings.setAmountSaved(updatedAmountSaved);
            personalSavingsRepository.save(personalSavings);

            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setAmount(amount);
            transactionHistory.setDate(LocalDate.now());
            transactionHistory.setName(personalSavings.getTarget());
            transactionHistory.setType(TransactionType.CREDIT);
            transactionHistory.setUser(users);
            transactionHistoryRepository.save(transactionHistory);

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("You are not authorized to add money to this savings", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add money to savings wallet.", e);
        }
    }

}
