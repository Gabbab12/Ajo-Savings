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
    @NotBlank(message = "please input your gender")
    private Gender gender;
    @Column(nullable = false)
    @NotBlank(message = "please input your occupation")
    private String occupation;
    @Column(nullable = false)
    @NotBlank(message = "please input your date of birth")
    private Date dateOfBirth;
    @Column(nullable = false)
    @NotBlank(message = "please input your means of identification")
    private IdentificationType identificationType;
    @Column(nullable = false)
    @NotBlank(message = "please input your bvn")
    private long bvn;
    @Column(nullable = false)
    @NotBlank(message = "please input your home address")
    private String address;
    @Column(nullable = false)
    @NotBlank(message = "please input your identification number")
    private String identificationNumber;
    @Column(nullable = false)
    @NotBlank(message = "please upload yor identification document")
    private String uploadIdentificationDocument;
    @Column(nullable = false)
    @NotBlank(message = "please upload the proof of your address")
    private String uploadProofOfAddress;


    @OneToOne
    private Users users;

}
