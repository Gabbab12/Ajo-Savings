package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.exception.BadRequestException;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.AjoGroupRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.AjoGroupService;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AjoGroupServiceImpl implements AjoGroupService {

    private final AjoGroupRepository ajoGroupRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<AjoGroup> createAjoGroup(AjoGroupDTO ajoGroupDTO) {
        if (ajoGroupDTO.getNumberOfParticipant() <= 1) {
            throw new BadRequestException("Number of participants must be greater than 1", HttpStatus.BAD_REQUEST);
        }
        AjoGroup ajoGroup = mapToEntity(ajoGroupDTO);

        if (ajoGroup.getUsers() == null) {
            ajoGroup.setUsers(new HashSet<>());
        }
        List<Integer> availableSlots = generateAvailableSlots(ajoGroup.getNumberOfParticipant());
        ajoGroup.setAvailableSlots(availableSlots);
        ajoGroup.setTotalSlot(ajoGroup.getNumberOfParticipant());
        ajoGroup.setEstimatedCollection(ajoGroup.getNumberOfParticipant() * ajoGroup.getContributionAmount());

        ajoGroup.setClosed(ajoGroup.getUsers().size() >= ajoGroup.getNumberOfParticipant());
        ajoGroupRepository.save(ajoGroup);

        return ResponseEntity.status(HttpStatus.CREATED).body(ajoGroup);
    }

    public static AjoGroup mapToEntity(AjoGroupDTO ajoGroupDTO) {
        AjoGroup ajoGroup = new AjoGroup();
        ajoGroup.setGroupName(ajoGroupDTO.getGroupName());
        ajoGroup.setContributionAmount(ajoGroupDTO.getContributionAmount());
        ajoGroup.setExpectedStartDate(ajoGroupDTO.getExpectedStartDate());
        ajoGroup.setExpectedEndDate(ajoGroupDTO.getExpectedEndDate());
        ajoGroup.setDuration(ajoGroupDTO.getDuration());
        ajoGroup.setNumberOfParticipant(ajoGroupDTO.getNumberOfParticipant());
        ajoGroup.setPaymentPeriod(ajoGroupDTO.getPaymentPeriod());
        ajoGroup.setTime(ajoGroupDTO.getTime());
        ajoGroup.setPurposeAndGoals(ajoGroupDTO.getPurposeAndGoals());

        return ajoGroup;
    }

    private List<Integer> updateAvailableSlots(AjoGroup ajoGroup) {
        List<Integer> allocatedSlots = ajoGroup.getUsers().stream()
                .map(Users::getAjoSlot)
                .toList();

        List<Integer> availableSlots = generateAvailableSlots(ajoGroup.getNumberOfParticipant());
        availableSlots.removeAll(allocatedSlots);

        return availableSlots;
    }
    private List<Integer> generateAvailableSlots(int numberOfSlots) {
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 1; i <= numberOfSlots; i++) {
            availableSlots.add(i);
        }
        return availableSlots;
    }
    @Override
    public ResponseEntity<AjoGroup> addUsers(Long groupId, Users user) {
        Optional<AjoGroup> optionalAjoGroup = ajoGroupRepository.findById(groupId);

        if (optionalAjoGroup.isEmpty()) {
            throw new ResourceNotFoundException("AjoGroup not found with id: " + groupId);
        }
        AjoGroup ajoGroup = optionalAjoGroup.get();

        if (ajoGroup.isClosed()) {
            throw new BadRequestException("AjoGroup is closed for new participant", HttpStatus.BAD_REQUEST);
        }
        if (ajoGroup.getUsers().contains(user)) {
            throw new BadRequestException("User is already part of the AjoGroup", HttpStatus.BAD_REQUEST);
        }
        int randomIndex = (int) (Math.random() * ajoGroup.getAvailableSlots().size());
        int allocatedSlot = ajoGroup.getAvailableSlots().remove(randomIndex);

        user.setAjoSlot(allocatedSlot);

        ajoGroup.getUsers().add(user);
        ajoGroup.setAvailableSlots(updateAvailableSlots(ajoGroup));

        ajoGroup.setClosed(ajoGroup.getUsers().size() >= ajoGroup.getNumberOfParticipant());

        ajoGroupRepository.save(ajoGroup);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(ajoGroup);
    }

}
