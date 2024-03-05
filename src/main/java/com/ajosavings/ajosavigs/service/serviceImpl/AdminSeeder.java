package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.configuration.PasswordConfig;
import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class AdminSeeder implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordConfig passwordConfig;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (userRepository.existsByRole(Role.ADMIN)){
            return;
        }
        Users admin = new Users();
        admin.setUsername("admin@gmail.com");
        admin.setFirstName("Gabriel");
        admin.setLastName("Ojo");
        admin.setPhoneNumber("08137354549");
        admin.setPassword(passwordConfig.passwordEncoder().encode("Admin12@"));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}
