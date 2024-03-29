package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.*;
import com.ajosavings.ajosavigs.dto.response.AuthenticationResponse;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.models.DefaultedUsers;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UsersService {
    ResponseEntity<Users> signUp(SignUpRequest signUpRequest);
    ResponseEntity<PasswordToken> forgotPassword(String email) throws MessagingException;
    boolean verifyPasswordToken(String token);
    @Transactional
    ResponseEntity<String> resetPassword(String token, PasswordDTO passwordDTO) throws ResourceNotFoundException;
    ResponseEntity<String> changePassword(PasswordChangeDTO PasswordChangeDTO);
    boolean oldPasswordIsValid(Users users, String oldPassword);
    ResponseEntity<AuthenticationResponse> loginRegisteredUser(LoginRequest request);
    ResponseEntity<Users> getUser(String email);
    void logout();
    ResponseEntity<String> updateProfilePicture(String profilePicture, String username);
    ResponseEntity<String> updateUserDetails(ProfileUpdateDto profileUpdateDto);

    ResponseEntity<Long> getTotalNumberOfUsers(Authentication authentication);
    long countNewUsers();

    Page<Users> getAllUsers(Pageable pageable);

    Page<Users> getAllActiveUsers(Pageable pageable);

    Page<Users> getAllNewUsers(Pageable pageable);

    Page<DefaultedUsers> getAllDefaultedUsers(Pageable pageable);

    long countNewlyDefaultingUsers();
}