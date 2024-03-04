package com.ajosavings.ajosavigs.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
