package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.GroupTransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class GroupTransactionHistory extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long slot;
    @Enumerated(EnumType.STRING)
    private GroupTransactionStatus status;
    private BigDecimal contributionAmount;
    @ManyToOne
    private AjoGroup ajoGroup;
    @ManyToOne
    private Users user;
}
