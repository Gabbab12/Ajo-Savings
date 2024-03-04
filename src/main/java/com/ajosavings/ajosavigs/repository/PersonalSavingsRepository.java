package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.PersonalSavings;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalSavingsRepository extends JpaRepository<PersonalSavings, Long> {
    Page<PersonalSavings> findByUsers(Users user, PageRequest pageRequest);
}
