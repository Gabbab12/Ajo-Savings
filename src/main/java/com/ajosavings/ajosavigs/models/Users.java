package com.ajosavings.ajosavigs.models;

import com.ajosavings.ajosavigs.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Users extends AuditBaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    @Email(message = "please provide a valid email")
    private String username;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    private String profilePicture;
    @Enumerated(EnumType.STRING)
    private Role role;
    private int ajoSlot;
    @DecimalMin(value = "0.00", inclusive = true, message = "Wallet balance must be at least 0.00")
    private BigDecimal globalWallet = BigDecimal.ZERO;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGroupSavings = BigDecimal.ZERO;
    @Column(nullable = false)
    @DecimalMin(value = "0.00", inclusive = true, message = "Wallet balance must be at least 0.00")
    private BigDecimal totalPersonalSavings = BigDecimal.ZERO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        return true;
    }
}
