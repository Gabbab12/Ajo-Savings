package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Page<TransactionHistory> findByUser(Users users, PageRequest pageRequest);

}
