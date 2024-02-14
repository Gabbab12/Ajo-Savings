package com.ajosavings.ajosavigs.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class PasswordConfig {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    public static boolean isValid(String password) {
        return password.matches(PASSWORD_PATTERN) && password.length() >= 8 && password.length() <= 20;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public boolean validatePassword(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
        }
    }
