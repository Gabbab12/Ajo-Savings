package com.ajosavings.ajosavigs.repository;

import com.ajosavings.ajosavigs.enums.GroupTransactionStatus;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupTransactionHistoryRepo extends JpaRepository<GroupTransactionHistory, Long> {
    Page<GroupTransactionHistory> findByAjoGroupId(Long AjoGroupId, Pageable pageable);
    Page<GroupTransactionHistory> findByAjoGroupAndStatus(AjoGroup ajoGroup, GroupTransactionStatus groupTransactionStatus, Pageable pageable);
    List<GroupTransactionHistory> findByAjoGroupAndCreatedAtIsAfter(AjoGroup ajoGroup, LocalDateTime localDateTime);

}
