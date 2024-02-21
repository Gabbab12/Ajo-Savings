package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.PersonalSavings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalSavingsRepository extends JpaRepository<PersonalSavings, Long> {
}
