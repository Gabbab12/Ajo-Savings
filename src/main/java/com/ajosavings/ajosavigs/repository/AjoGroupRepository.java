package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.AjoGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjoGroupRepository extends JpaRepository<AjoGroup, Long> {
    List<AjoGroup> findByUsersId(Long userId);

}
