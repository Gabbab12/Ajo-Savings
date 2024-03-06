package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.enums.TransactionType;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Page<TransactionHistory> findByUser(Users users, PageRequest pageRequest);

    List<TransactionHistory> findAllByType(TransactionType transactionType);

}
