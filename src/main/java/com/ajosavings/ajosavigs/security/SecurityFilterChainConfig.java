package com.ajosavings.ajosavigs.security;

import com.ajosavings.ajosavigs.configuration.JwtFilterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityFilterChainConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilterConfig filterConfiguration;
    private final CorsConfigurationSource corsConfigurationSource;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer->
                        configurer
                                .requestMatchers(
                                        "/v3/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html")
                                .permitAll()

                                .requestMatchers(POST,"/api/v1/auth/userReg").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signup/normal", "api/v1/auth/forgot").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signup/google").permitAll())
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(filterConfiguration, UsernamePasswordAuthenticationFilter.class);

        http.csrf(csrf->csrf.disable());
        http.cors().configurationSource(corsConfigurationSource);
//         http.cors(corsConfigurationSource-> corsConfigurationSource.disable());
        return http.build();
    }
}

