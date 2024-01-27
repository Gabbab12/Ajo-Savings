package com.ajosavings.ajosavigs.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

public class CorsConfig implements CorsConfigurationSource{
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("http://localhost:5174");
            config.addAllowedOrigin("http://localhost:5173");
            config.addAllowedOrigin("http://localhost:3000");

            config.addAllowedMethod("*");
            config.addAllowedHeader("*");
            config.setAllowCredentials(true);
            return config;
        }
}
