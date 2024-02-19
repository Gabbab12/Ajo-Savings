package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.DepositDto;
import com.ajosavings.ajosavigs.dto.request.TransactionRequest;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.TransactionOtp;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.dto.response.paystackPaymentInit.PaystackResponse;
import com.ajosavings.ajosavigs.repository.TransactionHistoryRepository;
import com.ajosavings.ajosavigs.repository.TransactionOtpRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final EmailServiceImpl emailService;
    private final TransactionOtpRepository otpRepository;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.paystack.co")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer sk_test_29d0c02151f8e2f3c0b99976ca2e78cc8cb0f03c")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                log.info("Request Headers: {}", clientRequest.headers());
                return Mono.just(clientRequest);
            }))
            .build();

    @Override
    public ResponseEntity<?> deposit(DepositDto depositDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        String userEmail = user.getUsername();

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setEmail(userEmail);
        transactionRequest.setAmount(depositDto.getAmount());

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(transactionRequest.getAmount());
        transactionHistory.setDate(LocalDate.now());
        transactionHistory.setName(user.getFirstName() + " " + user.getLastName());
        transactionHistory.setType("credit");
        transactionHistory.setUser(user);

        TransactionOtp transactionOtp = null;

        try {
            PaystackResponse initiateResponse = webClient
                    .post()
                    .uri("/transaction/initialize")
                    .bodyValue(transactionRequest)
                    .retrieve()
                    .bodyToMono(PaystackResponse.class)
                    .block();

            // Check if an OTP record already exists for the user
            Optional<TransactionOtp> existingOtpOptional = otpRepository.findByUsername(userEmail);
            if (existingOtpOptional.isPresent()) {
                transactionOtp = existingOtpOptional.get();
                transactionOtp.setAmount(transactionRequest.getAmount());
                transactionOtp.setTransactionOtp(generateTransactionOtp());
                transactionOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
                transactionOtp.setIsValid(true);
                otpRepository.save(transactionOtp);
            } else {
                transactionOtp = new TransactionOtp();
                transactionOtp.setAmount(transactionRequest.getAmount());
                transactionOtp.setTransactionOtp(generateTransactionOtp());
                transactionOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
                transactionOtp.setUsername(user.getUsername());
                transactionOtp.setIsValid(true);
                transactionOtp.setUsers(user);
                otpRepository.save(transactionOtp);
            }
            try {
                String content = "Dear user, find below the One Time Password to complete your transaction.\n" +
                        "\n" + transactionOtp.getTransactionOtp() +
                        "Please ignore if you di not request for this.\n";

                emailService.sendEmail(userEmail, "TRANSACTION OTP NUMBER", content);
            } catch (Exception emailEx) {
                log.error("Error sending email: {}", emailEx.getMessage());
            }

            if (initiateResponse != null && initiateResponse.getStatus()) {
                // Save transaction history
                transactionHistoryRepository.save(transactionHistory);
                return new ResponseEntity<>("Initiated transaction successfully. OTP sent to your email.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error initiating payment", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (WebClientResponseException ex) {
            return new ResponseEntity<>("Error initiating payment: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> verifyOtp(String transactionOtp, String username) {

        Users users = userRepository.findByUsername(username);
        Optional<TransactionOtp> otpOptional = otpRepository.findByUsername(users.getUsername());
        if (otpOptional.isEmpty()) {
            return new ResponseEntity<>("OTP not found or expired", HttpStatus.NOT_FOUND);
        }

        TransactionOtp transactionOtp1 = otpOptional.get();

        if (!transactionOtp1.getIsValid()) {
            return new ResponseEntity<>("OTP has already been used", HttpStatus.BAD_REQUEST);
        }
        if (!transactionOtp1.getTransactionOtp().equals(transactionOtp)) {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
        if (transactionOtp1.getExpiryTime().isBefore(LocalDateTime.now())) {
            transactionOtp1.setIsValid(false);
            otpRepository.save(transactionOtp1);
            return new ResponseEntity<>("OTP has expired", HttpStatus.BAD_REQUEST);
        }
        transactionOtp1.setIsValid(false);
        otpRepository.save(transactionOtp1);

        updateWalletBalance(transactionOtp1.getAmount(), transactionOtp1.getUsers());
        userRepository.save(users);

        return new ResponseEntity<>("OTP verification successful", HttpStatus.OK);
    }


    private String generateTransactionOtp() {
        return RandomStringUtils.randomNumeric(5);
    }

    private void updateWalletBalance(BigDecimal amount, Users user) {
        BigDecimal currentBalance = user.getGlobalWallet();
        BigDecimal newBalance = currentBalance.add(amount);
        user.setGlobalWallet(newBalance);
        userRepository.save(user);
    }

    private void WalletBalance(BigDecimal amount, Users user) {
        BigDecimal currentBalance = user.getGlobalWallet();
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setGlobalWallet(newBalance);
        userRepository.save(user);
    }

}
