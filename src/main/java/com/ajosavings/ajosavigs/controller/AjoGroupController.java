package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.exception.ResourceNotFoundException;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.AjoGroupRepository;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ajoGroup")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
                .orElseThrow(() -> new ResourceNotFoundException("AjoGroup with id " + ajoGroupId + " not found"));

        List<ContributionFlowDto> contributions = ajoGroupService.generateContributionsFlow(ajoGroup);
        return ResponseEntity.status(HttpStatus.OK).body(contributions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AjoGroup>> getGroupsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<AjoGroup> groups = ajoGroupService.getGroupsByUserId(userId);
        if (groups.isEmpty()) {
            throw new ResourceNotFoundException("No groups found for user with ID: " + userId);
        }
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

}
