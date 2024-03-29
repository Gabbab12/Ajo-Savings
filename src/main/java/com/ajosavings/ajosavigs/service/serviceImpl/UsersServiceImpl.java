package com.ajosavings.ajosavigs.service.serviceImpl;


import com.ajosavings.ajosavigs.configuration.JwtService;
import com.ajosavings.ajosavigs.configuration.PasswordConfig;
import com.ajosavings.ajosavigs.dto.request.*;
import com.ajosavings.ajosavigs.dto.response.AuthenticationResponse;
import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.enums.UserStatus;
import com.ajosavings.ajosavigs.exception.*;
import com.ajosavings.ajosavigs.models.DefaultedUsers;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.DefaultedUsersRepository;
import com.ajosavings.ajosavigs.repository.PasswordTokenRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.UsersService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final DefaultedUsersRepository defaultedUsersRepository;


    @Override
    public ResponseEntity<Users> signUp(SignUpRequest signUpRequest) {

        if (!passwordConfig.validatePassword(signUpRequest.getPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            throw new UserAlreadyExistException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        Users user = new Users();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordConfig.passwordEncoder().encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setProfilePicture("https://res.cloudinary.com/dpfqbb9pl/image/upload/v1708720135/gender_neutral_avatar_ruxcpg.jpg");
        user.setRole(Role.USERS);
        user.setStatus(UserStatus.PENDING_ACTIVATION);
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

        return ResponseEntity.ok(user);
    }

    public String generateOtpToken() {
        return RandomStringUtils.randomNumeric(6);
    }

    public String generatePasswordToken() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    @Override
    public ResponseEntity<PasswordToken> forgotPassword(String username) throws MessagingException {
        Optional<Users> user = userRepository.findUsersByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with this username: " + username, HttpStatus.NOT_FOUND);
        }

        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setToken(generateOtpToken());
        passwordToken.setUsername(user.get().getUsername());
        passwordToken.setUser(user.get());
        PasswordToken passwordToken1 = passwordTokenRepository.save(passwordToken);

        String content = "Dear user, \n"
                + "Find below the one time password to reset your password as requested \n"
                + passwordToken.getToken() + "\n" +
                "\n" + "Please ignore this message if you did not requested for this. \n";

        emailService.sendHTMLEmail(user.get().getUsername(), "RESET PASSWORD", content);

        return ResponseEntity.status(HttpStatus.OK).body(passwordToken1);
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
                passwordTokenRepository.save(passwordToken);
            } catch (Exception e) {
                throw new BadRequestException("Invalid OTP number", HttpStatus.BAD_REQUEST);
            }
        }
        return true;
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, PasswordDTO passwordDTO) throws ResourceNotFoundException {
        Optional<PasswordToken> passwordTokenOptional = passwordTokenRepository.findByTokenIgnoreCase(token);

        if (passwordTokenOptional.isEmpty()) {
            throw new ResourceNotFoundException("Invalid or expired reset password token", HttpStatus.NOT_FOUND);
        }
        PasswordToken passwordToken = passwordTokenOptional.get();

        // Check if the token is still valid and has not expired
        if (!passwordToken.getIsValid() || passwordToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired reset password token", HttpStatus.NOT_FOUND);
        }
        if (!passwordConfig.validatePassword(passwordDTO.getPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            Users user = userRepository.findByUsername(passwordDTO.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(passwordDTO.getPassword()));
            userRepository.save(user);

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
            throw new IncorrectOldPasswordException("Incorrect old password!", HttpStatus.BAD_REQUEST);
        }
        if (!PasswordConfig.isValid(PasswordChangeDTO.getNewPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(PasswordChangeDTO.getNewPassword(), PasswordChangeDTO.getConfirmNewPassword())) {
            throw new PasswordMismatchException("Password does not match!", HttpStatus.BAD_REQUEST);
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

        if (!users.isEnabled()) {
            throw new BadCredentialsException("User account is not yet verified or active");
        }

        String jwtToken = jwtService.generateToken(users);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtToken);
        response.setUser(users);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Users> getUser(String email) {
        Users users = userRepository.findByUsername(email);
        return ResponseEntity.ok(users);
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

    @Override
    public ResponseEntity<String> updateProfilePicture(String profilePicture, String username) {
        Optional<Users> user = userRepository.findUsersByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        user.get().setProfilePicture(profilePicture);

        userRepository.save(user.get());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        "Profile picture updated successfully"
                );
    }

    @Override
    public ResponseEntity<String> updateUserDetails(ProfileUpdateDto profileUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required.");
        }

        Users authenticatedUser = (Users) authentication.getPrincipal();

        if (!authenticatedUser.getUsername().equals(profileUpdateDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this profile.");
        }

        Optional<Users> optionalUser = userRepository.findUsersByUsername(profileUpdateDto.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Users user = optionalUser.get();

        user.setFirstName(profileUpdateDto.getFirstName());
        user.setLastName(profileUpdateDto.getLastName());
        user.setPhoneNumber(profileUpdateDto.getPhoneNumber());

        userRepository.save(user);
        log.info(String.valueOf(user));

        return ResponseEntity.status(HttpStatus.OK).body("Profile updated successfully.");
    }

    @Override
    public ResponseEntity<Long> getTotalNumberOfUsers(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    long getTotalUsers = userRepository.count();
                    log.info(String.valueOf(getTotalUsers));
                    return ResponseEntity.status(HttpStatus.OK).body(getTotalUsers);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public long countNewUsers() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        LocalDateTime endTime = LocalDateTime.now();

        return userRepository.countByCreatedAtBetween(startTime, endTime);
    }

    @Override
    public Page<Users> getAllUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    return userRepository.findAll(pageable);
                }
            }
        }
        throw new AccessDeniedException("User is not authorized to access this resource", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Page<Users> getAllActiveUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    return userRepository.findByStatus(UserStatus.ACTIVE, pageable);
                }
            }
        }
        throw new AccessDeniedException("User is not authorized to access this resource", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Page<Users> getAllNewUsers(Pageable pageable) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        LocalDateTime endTime = LocalDateTime.now();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return userRepository.findByCreatedAtBetween(startTime, endTime, pageable);
        } else {
            throw new AccessDeniedException("User is not authorized to access this resource", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Page<DefaultedUsers> getAllDefaultedUsers(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))){
            return defaultedUsersRepository.findAll(pageable);
        }
        throw new BadRequestException("You are not allowed to access this page", HttpStatus.BAD_REQUEST);
    }
    @Override
    public long countNewlyDefaultingUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            LocalDateTime startTime = LocalDateTime.now().minusHours(24);
            LocalDateTime endTime = LocalDateTime.now();

            return defaultedUsersRepository.countByCreatedAtBetween(startTime,endTime);
        }
        throw new BadRequestException("you are not allowed to view this page", HttpStatus.BAD_REQUEST);
    }
}
