package com.ajosavings.ajosavigs.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_savings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProjectSavings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long Id;

    @Column(name = "saving_target")
    private String savingTarget;

    @Column(name = "saving_target_amount")
    private BigDecimal savingTargetAmount;

    @Column(name = "saving_frequency")
    private Integer savingFrequency;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "withdrawal_date")
    private LocalDateTime withdrawalDate;

    @OneToOne
    @JoinColumn(name = "saving_goal_fk")
    private SavingsGoal savingsGoal;
}
