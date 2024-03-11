package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.enums.GroupTransactionStatus;
import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import com.ajosavings.ajosavigs.enums.TransactionType;
import com.ajosavings.ajosavigs.exception.AccessDeniedException;
import com.ajosavings.ajosavigs.exception.BadRequestException;
import com.ajosavings.ajosavigs.exception.InsufficientFundsException;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import com.ajosavings.ajosavigs.models.TransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.repository.AjoGroupRepository;
import com.ajosavings.ajosavigs.repository.GroupTransactionHistoryRepo;
import com.ajosavings.ajosavigs.repository.TransactionHistoryRepository;
import com.ajosavings.ajosavigs.repository.UserRepository;
import com.ajosavings.ajosavigs.service.AjoGroupService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AjoGroupServiceImpl implements AjoGroupService {

    private final AjoGroupRepository ajoGroupRepository;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final EntityManager entityManager;
    private final GroupTransactionHistoryRepo groupTransactionHistoryRepo;


    @Override
    @Transactional
    public ResponseEntity<AjoGroup> createAjoGroup(AjoGroupDTO ajoGroupDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        if (ajoGroupDTO.getNumberOfParticipant() <= 1) {
            throw new BadRequestException("Number of participants must be greater than 1", HttpStatus.BAD_REQUEST);
        }
        AjoGroup ajoGroup = mapToEntity(ajoGroupDTO);

        currentUser = entityManager.merge(currentUser);

        Set<Users> users = new HashSet<>();
        users.add(currentUser);
        ajoGroup.setUsers(users);

        List<Integer> availableSlots = generateAvailableSlots(ajoGroup.getNumberOfParticipant());
        Integer assignedSlot = assignSlotToUser(availableSlots);

        // Set assigned slot to the current user
        currentUser.setAjoSlot(assignedSlot);

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
        ajoGroup.setProfilePicture(ajoGroupDTO.getProfilePicture());
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

    private Integer assignSlotToUser(List<Integer> availableSlots) {
        if (availableSlots.isEmpty()) {
            throw new IllegalStateException("No available slots left in the group");
        }
        Integer assignedSlot = availableSlots.get(0);
        availableSlots.remove(assignedSlot);
        return assignedSlot;
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

    @Override
    public ResponseEntity<List<AjoGroup>> getAllGroups() {
        List<AjoGroup> groups = ajoGroupRepository.findAll();
        return ResponseEntity.ok(groups);
    }

    @Override
    public Optional<AjoGroup> getAjoGroupDetails(Long groupId) {
        return ajoGroupRepository.findById(groupId);
    }

    @Override
    public ResponseEntity<AjoGroup> makeContribution(Long ajoGroupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();

        AjoGroup ajoGroup = ajoGroupRepository.findById(ajoGroupId).orElseThrow(() ->
                new ResourceNotFoundException("AjoGroup with id " + ajoGroupId + " does not exist"));

        if (!ajoGroup.getUsers().contains(users)) {
            throw new AccessDeniedException("You are not a member of this Ajo Group", HttpStatus.NOT_ACCEPTABLE);
        }
        BigDecimal updatedGlobalWallet = users.getGlobalWallet().subtract(BigDecimal.valueOf(ajoGroup.getContributionAmount()));
        if (updatedGlobalWallet.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds in your global wallet: " + users.getGlobalWallet(), HttpStatus.BAD_REQUEST);
        }

        BigDecimal totalGroupSavings = users.getTotalGroupSavings().add(BigDecimal.valueOf(ajoGroup.getContributionAmount()));
        users.setTotalGroupSavings(totalGroupSavings);

        users.setGlobalWallet(updatedGlobalWallet);
        userRepository.save(users);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUser(users);
        transactionHistory.setName(ajoGroup.getGroupName());
        transactionHistory.setType(TransactionType.DEBIT);
        transactionHistory.setAmount(BigDecimal.valueOf(ajoGroup.getContributionAmount()));
        transactionHistory.setDate(LocalDate.now());
        log.info(String.valueOf(transactionHistory));
        transactionHistoryRepository.save(transactionHistory);

        GroupTransactionHistory groupTransactionHistory = new GroupTransactionHistory();
        groupTransactionHistory.setSlot(users.getAjoSlot());
        groupTransactionHistory.setUserId(String.format("USR%06d", users.getId()));
        groupTransactionHistory.setStatus(GroupTransactionStatus.RECEIVE);
        groupTransactionHistory.setContributionAmount(BigDecimal.valueOf(ajoGroup.getContributionAmount()));
        groupTransactionHistory.setName(users.getFirstName() + " " + users.getLastName());
        groupTransactionHistory.setAjoGroup(ajoGroup);
        log.info(String.valueOf(groupTransactionHistory));
        groupTransactionHistoryRepo.save(groupTransactionHistory);

        return ResponseEntity.status(HttpStatus.OK).body(ajoGroup);
    }

    @Override
    public List<ContributionFlowDto> generateContributionsFlow(AjoGroup ajoGroup) {
        List<ContributionFlowDto> contributions = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (Users user : ajoGroup.getUsers()) {
            ContributionFlowDto contributionFlowDto = new ContributionFlowDto();
            contributionFlowDto.setName(user.getFirstName() + " " + user.getLastName());
            contributionFlowDto.setContributing(BigDecimal.valueOf(ajoGroup.getContributionAmount()));
            contributionFlowDto.setWithdrawing(BigDecimal.ZERO);
            contributionFlowDto.setFee(BigDecimal.ZERO);

            int currentSlot = user.getAjoSlot();

            // Check if the user's slot matches the current slot
            if (currentSlot == calculateCurrentSlot(ajoGroup.getExpectedStartDate(), currentDate, ajoGroup.getPaymentPeriod())) {
                double fees = 0.01;
                BigDecimal withdrawingAmount = BigDecimal.valueOf(ajoGroup.getContributionAmount() * ajoGroup.getNumberOfParticipant());
                BigDecimal feeAmount = withdrawingAmount.multiply(BigDecimal.valueOf(fees));
                contributionFlowDto.setWithdrawing(withdrawingAmount);
                contributionFlowDto.setFee(feeAmount);
            }

            contributionFlowDto.setRecentCashOut(calculateRecentCashOutDate(ajoGroup.getExpectedStartDate(), currentDate, ajoGroup.getPaymentPeriod(), currentSlot));
            contributionFlowDto.setNextCashOut(calculateNextCashOutDate(ajoGroup.getExpectedStartDate(), currentDate, ajoGroup.getPaymentPeriod(), currentSlot));

            contributions.add(contributionFlowDto);
        }

        return contributions;
    }

    private int calculateCurrentSlot(Date expectedStartDate, LocalDate currentDate, PaymentPeriod paymentPeriod) {
        LocalDate startDate = expectedStartDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long daysSinceStart = ChronoUnit.DAYS.between(startDate, currentDate);

        return (int) (daysSinceStart / getSlotDuration(paymentPeriod)) + 1;
    }

    private LocalDate calculateRecentCashOutDate(Date expectedStartDate, LocalDate currentDate, PaymentPeriod paymentPeriod, int currentSlot) {
        return calculateNextCashOutDate(expectedStartDate, currentDate, paymentPeriod, currentSlot - 1); // Subtract 1 to get the previous slot
    }

    private LocalDate calculateNextCashOutDate(Date expectedStartDate, LocalDate currentDate, PaymentPeriod paymentPeriod, int currentSlot) {
        LocalDate startDate = expectedStartDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return startDate.plusDays(getSlotDuration(paymentPeriod) * currentSlot);
    }

    private long getSlotDuration(PaymentPeriod paymentPeriod) {
        return switch (paymentPeriod) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
        };
    }

    @Override
    public List<AjoGroup> getGroupsByUserId(Long userId) {
        return ajoGroupRepository.findAll().stream()
                .filter(ajoGroup -> containsUserWithId(ajoGroup, userId))
                .collect(Collectors.toList());
    }

    private boolean containsUserWithId(AjoGroup ajoGroup, Long userId) {
        return ajoGroup.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    @Override
    public ResponseEntity<Long> getTotalSavingGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    long totalCount = ajoGroupRepository.count();
                    log.info(String.valueOf(totalCount));
                    return ResponseEntity.ok(totalCount);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public ResponseEntity<Double> getTotalContributions(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    List<Users> users = userRepository.findAll();
                    double totalContrbutions = 0.0;
                    for (Users user : users) {
                        totalContrbutions += user.getTotalGroupSavings().doubleValue();
                    }
                    log.info(String.valueOf(totalContrbutions));
                    return ResponseEntity.status(HttpStatus.OK).body(totalContrbutions);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public ResponseEntity<Long> getNewAjoGroups(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
                    LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX);
                    long totalGroupNumber = ajoGroupRepository.countByCreatedAtBetween(startOfToday, endOfToday);
                    return ResponseEntity.status(HttpStatus.OK).body(totalGroupNumber);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public Page<GroupTransactionHistory> getGroupTransactionHistory(Long groupId, Authentication authentication, Pageable pageable) {
        Optional<AjoGroup> optionalAjoGroup = ajoGroupRepository.findById(groupId);
        if (optionalAjoGroup.isEmpty()) {
            throw new ResourceNotFoundException("AjoGroup with id " + groupId + " does not exist");
        }
        AjoGroup ajoGroup = optionalAjoGroup.get();
        Users currentUser = (Users) authentication.getPrincipal();

        if (!ajoGroup.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this Ajo Group", HttpStatus.FORBIDDEN);
        }
        return groupTransactionHistoryRepo.findByAjoGroupId(groupId, pageable);
    }

    @Override
    public Page<GroupTransactionHistory> getGroupReceivedTransactions(Long groupId, Pageable pageable) {
        AjoGroup ajoGroup = ajoGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("AjoGroup with id " + groupId + " not found"));

        return groupTransactionHistoryRepo.findByAjoGroupAndStatus(ajoGroup, GroupTransactionStatus.RECEIVE, pageable);
    }
}
