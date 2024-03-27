package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.GroupTransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class GroupTransactionHistory extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long slot;
    private String userId;
    private String name;
    @Enumerated(EnumType.STRING)
    private GroupTransactionStatus status;
    private BigDecimal contributionAmount;
    @ManyToOne
    private AjoGroup ajoGroup;
    @ManyToOne
    private Users user;
}
