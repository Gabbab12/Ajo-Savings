package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.dto.response.GroupTransactionPage;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AjoGroupService {
    ResponseEntity<AjoGroup> createAjoGroup(AjoGroupDTO ajoGroupDTO);

    ResponseEntity<AjoGroup> addUsers(Long groupId, Users users);

    ResponseEntity<List<AjoGroup>> getAllGroups();

    Optional<AjoGroup> getAjoGroupDetails(Long groupId);

    ResponseEntity<AjoGroup> makeContribution(Long ajoGroupId);

    @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    void processGroupPayments();

    List<ContributionFlowDto> generateContributionsFlow(AjoGroup ajoGroup);

    List<AjoGroup> getGroupsByUserId(Long userId);

    ResponseEntity<Long> getTotalSavingGroups();

    ResponseEntity<Double> getTotalContributions(Authentication authentication);


    ResponseEntity<Long> getNewAjoGroups(Authentication authentication);

    GroupTransactionPage getGroupTransactionHistory(Long groupId, Authentication authentication, Pageable pageable);

    Page<GroupTransactionHistory> getGroupReceivedTransactions(Long groupId, Pageable pageable);

    Page<GroupTransactionHistory> getGroupSentTransactions(Long groupId, Pageable pageable);

    @Transactional
    ResponseEntity<AjoGroup> updateAjoGroup(Long ajoGroupId, AjoGroupDTO updatedAjoGroupDTO);

    @Transactional
    ResponseEntity<Void> deleteAjoGroup(Long ajoGroupId);

    ResponseEntity<AjoGroup> enableAjoGroup(Long ajoGroupId);

    ResponseEntity<AjoGroup> disableAjoGroup(Long ajoGroupId);
}

