package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.AjoGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AjoGroupRepository extends JpaRepository<AjoGroup, Long> {
    List<AjoGroup> findByUsersId(Long userId);
    long count();

    long countByCreatedAtBetween(LocalDateTime startOfDay,  LocalDateTime endOfDay);


}
