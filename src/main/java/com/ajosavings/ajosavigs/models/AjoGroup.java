package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    private double contributionAmount;
    @Column(nullable = false)
    private Date expectedStartDate;
    @Column(nullable = false)
    private Date expectedEndDate;
    @Column(nullable = false)
    @NotBlank(message = "please input the duration of the Ajo")
    private String duration;
    @Column(nullable = false)
    private int numberOfParticipant;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentPeriod paymentPeriod;
    @Column(nullable = false)
    private LocalTime time;
    @Column(nullable = false)
    @NotBlank(message = "please enter the purpose and goals of this group")
    private String purposeAndGoals;
    private double estimatedCollection;

    private String profilePicture;

    private int totalSlot;

    private boolean closed;

    @ElementCollection
    @CollectionTable(name = "available_slots", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "available_slot")
    private List<Integer> availableSlots;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_ajo_group",
            joinColumns = @JoinColumn(name = "ajo_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> users = new HashSet<>();

}

