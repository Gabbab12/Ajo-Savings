package com.ajosavings.ajosavigs.service.serviceImpl;



import com.ajosavings.ajosavigs.configuration.PasswordConfig;
import com.ajosavings.ajosavigs.dto.PasswordDTO;
import com.ajosavings.ajosavigs.dto.SignUpRequest;
import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.exception.UserNotFoundException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        return userRepository.save(user);

    }


    public String generatePasswordToken(){
        String alphanumerictoken = RandomStringUtils.randomAlphanumeric(16);
        return alphanumerictoken;
    }
    @Override
    public PasswordToken forgotPassword(String username) throws MessagingException{
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found with this username: " + username);
        }
        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setToken(generatePasswordToken());
        passwordToken.setUsername(username);
        PasswordToken passwordToken1 = passwordTokenRepository.save(passwordToken);
        emailService.sendForgotPasswordEmail(user.getUsername(), passwordToken);
        return passwordToken1;
    }

    @Override
    public boolean verifyPasswordToken(String token) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(token).orElseThrow(() ->
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
    @Transactional
    @Override
    public ResponseEntity<String> resetPassword(String token, PasswordDTO passwordDTO) throws ResourceNotFoundException {
        Optional<PasswordToken> passwordToken = passwordTokenRepository.findByToken(token);
        if (passwordToken.isEmpty()) {
            throw new ResourceNotFoundException("Invalid reset password token");
        }
        if (Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            Users user = userRepository.findByUsername(passwordDTO.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(passwordDTO.getPassword()));
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
