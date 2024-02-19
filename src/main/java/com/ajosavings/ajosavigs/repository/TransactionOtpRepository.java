package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.TransactionOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionOtpRepository extends JpaRepository<TransactionOtp, Long> {

    Optional<TransactionOtp> findByUsername(String userEmail);

}
