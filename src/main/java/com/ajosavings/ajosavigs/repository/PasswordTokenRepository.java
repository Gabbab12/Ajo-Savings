package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
//    Optional<PasswordToken> findByToken(String token);

    Optional<PasswordToken> findByVerificationToken(String verificationToken);

    Optional<PasswordToken> findByTokenIgnoreCase(String token);
}


