package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.KYCUpdates;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycUpdateRepository extends JpaRepository<KYCUpdates, Long> {
    Optional<KYCUpdates> findByUsers(Users user);

}
