package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PersonalSavings extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String target;
    private BigDecimal targetAmount;
    @Enumerated(EnumType.STRING)
    private PaymentPeriod frequency;
    private LocalDate startDate;
    private LocalDate withdrawalDate;
    @DecimalMin(value = "0.00", inclusive = true, message = "Wallet balance must be at least 0.00")
    private BigDecimal amountSaved = BigDecimal.ZERO;
    private long daysLeft;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users users;
}
