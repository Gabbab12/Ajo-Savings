package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AjoGroupService {
    ResponseEntity<AjoGroup> createAjoGroup(AjoGroupDTO ajoGroupDTO);

    ResponseEntity<AjoGroup> addUsers(Long groupId, Users users);

    ResponseEntity<List<AjoGroup>> getAllGroups();

    Optional<AjoGroup> getAjoGroupDetails(Long groupId);

    ResponseEntity<AjoGroup> makeContribution(Long ajoGroupId);

    List<ContributionFlowDto> generateContributionsFlow(AjoGroup ajoGroup);

    List<AjoGroup> getGroupsByUserId(Long userId);

    ResponseEntity<Long> getTotalSavingGroups();

 endpoint-for-defaulted-user
    ResponseEntity<List<Users>> getDefaultedUsers(Long ajoGroupId, Authentication authentication);

    ResponseEntity<Double> getTotalContributions(Authentication authentication);


    ResponseEntity<Long> getNewAjoGroups(Authentication authentication);
 main
}

