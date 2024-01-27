package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsernameIgnoreCase(String username);

    Users findByUsername(String username);
}
