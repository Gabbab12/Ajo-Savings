package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingGoalRepository extends JpaRepository<SavingsGoal,Long> {
}
