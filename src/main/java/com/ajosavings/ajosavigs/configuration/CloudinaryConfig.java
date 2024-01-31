package com.ajosavings.ajosavigs.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${CLOUD_NAME}")
    private String cloud_name;

    @Value("${API_KEY}")
    private String api_key;

    @Value("${API_SECRET}")
    private String api_secret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", cloud_name);
        config.put("api_key", api_key);
        config.put("api_secret", api_secret);

        return new Cloudinary(config);
    }
}
