package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.enums.UserStatus;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsernameIgnoreCase(String username);
    Users findByUsername(String username);

    Optional<Users> findUsersByUsername(String username);

    boolean existsByRole(Role role);

    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    Page<Users> findByStatus(UserStatus userStatus, Pageable pageable);

    Page<Users> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    boolean existsByUsername(String username);

}
