package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ajoGroup")
public class AjoGroupController {
    private final AjoGroupServiceImpl ajoGroupService;

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
}
