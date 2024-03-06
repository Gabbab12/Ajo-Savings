package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsernameIgnoreCase(String username);
    Users findByUsername(String username);

    Optional<Users> findUsersByUsername(String username);

    boolean existsByRole(Role role);
    int countByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
}
