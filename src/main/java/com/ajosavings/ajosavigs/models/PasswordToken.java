package com.ajosavings.ajosavigs.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
public class PasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String verificationToken;
    private Boolean isValid = true;
    private String username;
    private LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}