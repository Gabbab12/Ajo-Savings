package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class AjoGroup extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "please input the group name")
    private String groupName;
    @Column(nullable = false)
    @NotBlank(message = "please input the contribution amount")
    private double contributionAmount;
    @Column(nullable = false)
    @NotBlank(message = "please input your the expected starting date")
    private Date expectedStartDate;
    @Column(nullable = false)
    @NotBlank(message = "please input the expected end date")
    private Date expectedEndDate;
    @Column(nullable = false)
    @NotBlank(message = "please input the duration of the Ajo")
    private int duration;
    @Column(nullable = false)
    @NotBlank(message = "please input the number of the participants you want in this group")
    private int numberOfParticipant;
    @Column(nullable = false)
    @NotBlank(message = "please click on the payment period of this savings")
    private PaymentPeriod paymentPeriod;
    @Column(nullable = false)
    @NotBlank(message = "please enter the time")
    private LocalTime time;
    @Column(nullable = false)
    @NotBlank(message = "please enter the purpose and goals of this group")
    private String purposeAndGoals;


    @OneToMany(mappedBy = "ajoGroup", cascade = {CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Users> users;

}

