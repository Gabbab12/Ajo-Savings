package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.DefaultedUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultedUsersRepository extends JpaRepository<DefaultedUsers, Long> {
}
