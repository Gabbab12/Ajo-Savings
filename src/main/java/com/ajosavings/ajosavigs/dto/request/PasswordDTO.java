package com.ajosavings.ajosavigs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private String password;
    private String confirmPassword;
    private String username;
}
