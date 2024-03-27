package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.dto.response.GroupTransactionPage;
import com.ajosavings.ajosavigs.enums.Role;
import com.ajosavings.ajosavigs.exception.AccessDeniedException;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.AjoGroupRepository;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ajoGroup")
@Slf4j
public class AjoGroupController {
    private final AjoGroupServiceImpl ajoGroupService;
    private final AjoGroupRepository ajoGroupRepository;

    @PostMapping("/create-ajoGroup")
    public ResponseEntity<AjoGroup> createAjoGroup(@RequestBody AjoGroupDTO ajoGroupDTO) {
        return ajoGroupService.createAjoGroup(ajoGroupDTO);
    }

    @PostMapping("/add-user/{groupId}")
    public ResponseEntity<AjoGroup> addUserToGroup(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("you are not authorized to join this group", HttpStatus.UNAUTHORIZED);
        }
        Users user = (Users) authentication.getPrincipal();
        ResponseEntity<AjoGroup> response = ajoGroupService.addUsers(groupId, user);

        return response;
    }

    @GetMapping("/explore")
    public ResponseEntity<List<AjoGroup>> getAllGroups() {
        return ajoGroupService.getAllGroups();
    }

    @GetMapping("/groupDetails/{groupId}")
    public ResponseEntity<AjoGroup> getAjoGroupDetails(@PathVariable Long groupId) {
        Optional<AjoGroup> optionalAjoGroup = ajoGroupService.getAjoGroupDetails(groupId);

        return optionalAjoGroup.map(ajoGroup -> ResponseEntity.ok().body(ajoGroup))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/contribute/{ajoGroupId}")
    public ResponseEntity<AjoGroup> makeContribution(@PathVariable Long ajoGroupId) {
        return ajoGroupService.makeContribution(ajoGroupId);
    }

    @GetMapping("/{ajoGroupId}/contributions")
    public ResponseEntity<List<ContributionFlowDto>> getContributions(@PathVariable Long ajoGroupId) throws ResourceNotFoundException {
        AjoGroup ajoGroup = ajoGroupRepository.findById(ajoGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("AjoGroup with id " + ajoGroupId + " not found", HttpStatus.NOT_FOUND));

        List<ContributionFlowDto> contributions = ajoGroupService.generateContributionsFlow(ajoGroup);
        return ResponseEntity.status(HttpStatus.OK).body(contributions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AjoGroup>> getGroupsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<AjoGroup> groups = ajoGroupService.getGroupsByUserId(userId);
        if (groups.isEmpty()) {
            throw new ResourceNotFoundException("No groups found for user with ID: " + userId, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/get-group-transaction/{groupId}")
    public ResponseEntity<GroupTransactionPage> getGroupTransactionHistory(@PathVariable Long groupId, Authentication authentication,
                                                                           @PageableDefault(size = 10, page = 0) Pageable pageable) {
        GroupTransactionPage transactionHistoryPage = ajoGroupService.getGroupTransactionHistory(groupId, authentication, pageable);
        return ResponseEntity.ok(transactionHistoryPage);
    }

    @PutMapping("/update-group/{ajoGroupId}")
    public ResponseEntity<AjoGroup> editAjoGroup(@PathVariable Long ajoGroupId, @RequestBody AjoGroupDTO updatedAjoGroupDTO){
        return ajoGroupService.updateAjoGroup(ajoGroupId,updatedAjoGroupDTO);
    }

    @DeleteMapping("delete-group/{ajoGroupId}")
    public ResponseEntity<?> deleteAjoGroup(@PathVariable Long ajoGroupId){
        return ajoGroupService.deleteAjoGroup(ajoGroupId);
    }
}
