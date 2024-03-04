package com.ajosavings.ajosavigs.service.serviceImpl;


import com.ajosavings.ajosavigs.configuration.JwtService;
import com.ajosavings.ajosavigs.configuration.PasswordConfig;
import com.ajosavings.ajosavigs.dto.request.LoginRequest;
import com.ajosavings.ajosavigs.dto.request.PasswordChangeDTO;
import com.ajosavings.ajosavigs.dto.request.PasswordDTO;
import com.ajosavings.ajosavigs.dto.request.SignUpRequest;
import com.ajosavings.ajosavigs.dto.response.AuthenticationResponse;
import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.exception.*;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.PasswordTokenRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.UsersService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final PasswordTokenRepository passwordTokenRepository;
    private final EmailServiceImpl emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users signUp(SignUpRequest signUpRequest) {

        if (!passwordConfig.validatePassword(signUpRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid password format");
        }
        Users user = new Users();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setRole(Role.USERS);
        log.info(String.valueOf(user));
        userRepository.save(user);

        Optional<PasswordToken> existingToken = passwordTokenRepository.findByUsername(user.getUsername());
        PasswordToken passwordToken = existingToken.orElseGet(() -> {
            // Generate a new token if none exists
            PasswordToken newToken = new PasswordToken();
            newToken.setVerificationToken(generateOtpToken());
            newToken.setUsername(user.getUsername());
            return newToken;
        });
        passwordTokenRepository.save(passwordToken);

        String content = "Dear user, we at AjoSavings welcome you to this uncommon ways of saving your income.\n" +
                "\n" +
                "Please enter the below verification code to complete your registration\n" +
                "\n" + passwordToken.getVerificationToken();

        emailService.sendEmail(user.getUsername(), "VERIFICATION EMAIL", content);

        return user;
    }

    public String generateOtpToken() {
        return RandomStringUtils.randomNumeric(6);
    }

    public String generatePasswordToken() {
        String alphanumerictoken = RandomStringUtils.randomAlphanumeric(16);
        return alphanumerictoken;
    }

    @Override
    public PasswordToken forgotPassword(String username) throws MessagingException {
        Optional<Users> user = userRepository.findUsersByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with this username: " + username, HttpStatus.NOT_FOUND);
        }

        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setToken(generatePasswordToken());
        passwordToken.setUsername(user.get().getUsername());
        passwordToken.setUser(user.get());
        PasswordToken passwordToken1 = passwordTokenRepository.save(passwordToken);
        emailService.sendForgotPasswordEmail(user.get().getUsername(), passwordToken);
        return passwordToken1;
    }

    @Override
    public boolean verifyPasswordToken(String token) {
        PasswordToken passwordToken = passwordTokenRepository.findByTokenIgnoreCase(token).orElseThrow(() ->
                new NullPointerException("Token is invalid"));
        if (passwordToken.getIsValid().equals(false)) {
            return false;
        }
        if (passwordToken.getExpirationTime().isAfter(LocalDateTime.now())) {
            try {
                passwordToken.setIsValid(false);
                passwordTokenRepository.save(passwordToken);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, PasswordDTO passwordDTO) throws ResourceNotFoundException {
        Optional<PasswordToken> passwordTokenOptional = passwordTokenRepository.findByTokenIgnoreCase(token);

        if (passwordTokenOptional.isEmpty()) {
            throw new ResourceNotFoundException("Invalid or expired reset password token");
        }
        PasswordToken passwordToken = passwordTokenOptional.get();

        // Check if the token is still valid and has not expired
        if (!passwordToken.getIsValid() || passwordToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired reset password token");
        }
        if (!passwordConfig.validatePassword(passwordDTO.getPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            Users user = userRepository.findByUsername(passwordDTO.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(passwordDTO.getPassword()));
            userRepository.save(user);

            // set the token isvalid to false after use
            passwordToken.setIsValid(false);
            passwordTokenRepository.save(passwordToken);
        }

        return new ResponseEntity<>("Password successfully reset", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(PasswordChangeDTO PasswordChangeDTO) {
        Optional<Users> targetUser = userRepository.findById(PasswordChangeDTO.getUserId());
        if (targetUser.isEmpty()) {
            throw new UserNotFoundException("User not found", HttpStatus.NOT_FOUND);
        }
        Users users = targetUser.get();
        if (!oldPasswordIsValid(users, PasswordChangeDTO.getOldPassword())) {
            throw new IncorrectOldPasswordException("Incorrect old password!");
        }
        if (!PasswordConfig.isValid(PasswordChangeDTO.getNewPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(PasswordChangeDTO.getNewPassword(), PasswordChangeDTO.getConfirmNewPassword())) {
            throw new PasswordMismatchException("Password does not match!");
        }
        users.setPassword(passwordEncoder.encode(PasswordChangeDTO.getNewPassword()));
        userRepository.save(users);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Override
    public boolean oldPasswordIsValid(Users user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }


    @Override
    public ResponseEntity<AuthenticationResponse> loginRegisteredUser(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Users users = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));

        String jwtToken = jwtService.generateToken(users);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtToken);
        response.setUser(users);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Users> getUser(String email) {
        Users users = userRepository
                .findByUsername(email);

        return ResponseEntity.ok(
               users
        );
    }




    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Users user = userRepository.findByUsernameIgnoreCase(email).get();
            System.out.println(user);
        }
        SecurityContextHolder.clearContext();
    }

}
