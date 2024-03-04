package com.ajosavings.ajosavigs.dto.response;

import com.ajosavings.ajosavigs.models.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AuthenticationResponse {
    @JsonProperty("token")
    private String token;
    private Users user;
}
