package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.DefaultedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DefaultedUsersRepository extends JpaRepository<DefaultedUsers, Long> {
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}
