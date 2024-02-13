package com.ajosavings.ajosavigs.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_savings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long Id;

    @Column(name = "saving_goal")
    private String savingGoal;

    @ManyToOne
    @JoinColumn(name = "project_savings_fk")
    private ProjectSavings projectSavings;

}
