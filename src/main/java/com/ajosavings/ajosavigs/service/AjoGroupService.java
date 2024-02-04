package com.ajosavings.ajosavigs.service;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import org.springframework.http.ResponseEntity;

public interface AjoGroupService {
    ResponseEntity<AjoGroup> createAjoGroup(AjoGroupDTO ajoGroupDTO);

    ResponseEntity<AjoGroup> addUsers(Long groupId, Users users);
}
