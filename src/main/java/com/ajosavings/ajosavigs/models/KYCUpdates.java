package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.Gender;
import com.ajosavings.ajosavigs.enums.IdentificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class KYCUpdates extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    @NotBlank(message = "please input your occupation")
    private String occupation;
    @Column(nullable = false)
    private Date dateOfBirth;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentificationType identificationType;
    @Column(nullable = false)
    private long bvn;
    @Column(nullable = false)
    @NotBlank(message = "please input your home address")
    private String address;
    @Column(nullable = false)
    @NotBlank(message = "please input your identification number")
    private String identificationNumber;
    @Column(nullable = false)
    private String uploadIdentificationDocument;
    @Column(nullable = false)
    private String uploadProofOfAddress;


    @OneToOne
    private Users users;

}
