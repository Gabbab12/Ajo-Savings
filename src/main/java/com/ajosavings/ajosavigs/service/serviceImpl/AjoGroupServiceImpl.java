package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.dto.request.AjoGroupDTO;
import com.ajosavings.ajosavigs.dto.request.ContributionFlowDto;
import com.ajosavings.ajosavigs.enums.*;
import com.ajosavings.ajosavigs.exception.*;
import com.ajosavings.ajosavigs.models.*;
import com.ajosavings.ajosavigs.repository.*;
import com.ajosavings.ajosavigs.service.AjoGroupService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
    private final DefaultedUsersRepository defaultedUsersRepository;


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

        currentUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(currentUser);

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
            throw new ResourceNotFoundException("AjoGroup not found with id: " + groupId, HttpStatus.NOT_FOUND);
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
        user.setStatus(UserStatus.ACTIVE);

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
                new ResourceNotFoundException("AjoGroup with id " + ajoGroupId + " does not exist", HttpStatus.NOT_FOUND));

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

        recordTransactionHistory(users, ajoGroup.getGroupName(), BigDecimal.valueOf(ajoGroup.getContributionAmount()), TransactionType.CREDIT);
        recordGroupTransactionHistory(users, ajoGroup, BigDecimal.valueOf(ajoGroup.getContributionAmount()), GroupTransactionStatus.RECEIVE);


        return ResponseEntity.status(HttpStatus.OK).body(ajoGroup);
    }
    public void recordGroupTransactionHistory(Users user, AjoGroup ajoGroup, BigDecimal contributionAmount, GroupTransactionStatus status) {
        GroupTransactionHistory groupTransactionHistory = new GroupTransactionHistory();
        groupTransactionHistory.setSlot(user.getAjoSlot());
        groupTransactionHistory.setStatus(status);
        groupTransactionHistory.setContributionAmount(contributionAmount);
        groupTransactionHistory.setAjoGroup(ajoGroup);
        groupTransactionHistory.setUser(user);
        groupTransactionHistoryRepo.save(groupTransactionHistory);
    }

    public void recordTransactionHistory(Users user, String name, BigDecimal amount, TransactionType type) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUser(user);
        transactionHistory.setName(name);
        transactionHistory.setType(type);
        transactionHistory.setAmount(amount);
        transactionHistory.setDate(LocalDate.now());
        transactionHistoryRepository.save(transactionHistory);
    }
    @Override
    @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    public void processGroupPayments() {
        List<AjoGroup> ajoGroups = ajoGroupRepository.findAll();

        for (AjoGroup ajoGroup : ajoGroups) {
            LocalDate expectedEndDate = calculateExpectedEndDate(ajoGroup);

            if (isContributionPeriodEnded(ajoGroup, expectedEndDate)) {
                BigDecimal totalContributionAmount = BigDecimal.valueOf(ajoGroup.getEstimatedCollection());
                BigDecimal interestRate = BigDecimal.valueOf(0.05);

                Set<Integer> paidSlots = new HashSet<>();
                Set<Users> paidUsers = findPaidUsers(ajoGroup, expectedEndDate);
                List<Users> defaultedUsers = new ArrayList<>();

                for (Users user : ajoGroup.getUsers()) {
                    if (!paidUsers.contains(user)) {
                        // User hasn't made payment, mark as defaulted
                        DefaultedUsers defaultedUser = new DefaultedUsers();
                        defaultedUser.setUserId(user.getId()); // Assuming user.getId() returns the user's ID
                        defaultedUser.setName(user.getFirstName() + " " + user.getLastName());
                        defaultedUser.setUsername(user.getUsername());
                        defaultedUser.setLastLogin(LocalDateTime.now());

                        defaultedUsersRepository.save(defaultedUser);
                        defaultedUsers.add(user);
                    } else {

                        BigDecimal interestDeduction = totalContributionAmount.multiply(interestRate);
                        BigDecimal finalPayment = totalContributionAmount.subtract(interestDeduction);

                        BigDecimal updatedGlobalWallet = user.getGlobalWallet().add(finalPayment);
                        user.setGlobalWallet(updatedGlobalWallet);
                        userRepository.save(user);

                        // Record transaction history
                        recordTransactionHistory(user, ajoGroup.getGroupName(), finalPayment, TransactionType.CREDIT);
                        recordGroupTransactionHistory(user, ajoGroup, finalPayment, GroupTransactionStatus.SENT);

                        paidSlots.add(user.getAjoSlot());
                    }
                }
            }
        }
    }

    private Set<Users> findPaidUsers(AjoGroup ajoGroup, LocalDate expectedEndDate) {
        List<GroupTransactionHistory> transactions = groupTransactionHistoryRepo.findByAjoGroupAndCreatedAtIsAfter(ajoGroup, expectedEndDate.atStartOfDay());
        Set<Users> paidUsers = new HashSet<>();
        for (GroupTransactionHistory transaction : transactions) {
            paidUsers.add((Users) transaction.getAjoGroup().getUsers());
        }
        return paidUsers;
    }

    private LocalDate calculateExpectedEndDate(AjoGroup ajoGroup) {
        Date startDate = ajoGroup.getExpectedStartDate();
        Instant instant = startDate.toInstant();
        LocalDate expectedEndDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        expectedEndDate = switch (ajoGroup.getPaymentPeriod()) {
            case MONTHLY -> expectedEndDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            case WEEKLY -> expectedEndDate.plusWeeks(1);
            case DAILY -> expectedEndDate.plusDays(1);
        };
        return expectedEndDate;
    }

    private boolean isContributionPeriodEnded(AjoGroup ajoGroup, LocalDate expectedEndDate) {
        return LocalDate.now().isAfter(expectedEndDate);
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
            throw new ResourceNotFoundException("AjoGroup with id " + groupId + " does not exist", HttpStatus.NOT_FOUND);
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
                .orElseThrow(() -> new ResourceNotFoundException("AjoGroup with id " + groupId + " not found", HttpStatus.NOT_FOUND));
        return groupTransactionHistoryRepo.findByAjoGroupAndStatus(ajoGroup, GroupTransactionStatus.RECEIVE, pageable);
    }
    @Override
    public Page<GroupTransactionHistory> getGroupSentTransactions(Long groupId, Pageable pageable) {
        AjoGroup ajoGroup = ajoGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("AjoGroup with id " + groupId + " not found", HttpStatus.NOT_FOUND));
        return groupTransactionHistoryRepo.findByAjoGroupAndStatus(ajoGroup, GroupTransactionStatus.SENT, pageable);
    }
    @Override
    @Transactional
    public ResponseEntity<AjoGroup> updateAjoGroup(Long ajoGroupId, AjoGroupDTO updatedAjoGroupDTO) {

        AjoGroup existingAjoGroup = ajoGroupRepository.findById(ajoGroupId)
                .orElseThrow(() -> new BadRequestException("AjoGroup not found with id",HttpStatus.BAD_REQUEST));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        existingAjoGroup.setGroupName(updatedAjoGroupDTO.getGroupName());

        AjoGroup updatedAjoGroup = ajoGroupRepository.save(existingAjoGroup);

        return ResponseEntity.ok(updatedAjoGroup);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteAjoGroup(Long ajoGroupId) {

        AjoGroup ajoGroup = ajoGroupRepository.findById(ajoGroupId)
                .orElseThrow(() -> new NotFoundException("AjoGroup not found with id",HttpStatus.NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Only admin users can delete AjoGroups",HttpStatus.FORBIDDEN);
        }

        ajoGroupRepository.deleteById(ajoGroup.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<AjoGroup> enableAjoGroup(Long ajoGroupId) {
        Optional<AjoGroup> optionalAjoGroup = ajoGroupRepository.findById(ajoGroupId);
        if (optionalAjoGroup.isEmpty()) {
            throw new ResourceNotFoundException("AjoGroup with id " + ajoGroupId + " does not exist", HttpStatus.NOT_FOUND);
        }
        AjoGroup ajoGroup = optionalAjoGroup.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(authentication)) {
            throw new AccessDeniedException("Only admin users can enable Ajo groups", HttpStatus.FORBIDDEN);
        }

        ajoGroup.setEnabled(true);
        ajoGroupRepository.save(ajoGroup);

        return ResponseEntity.ok(ajoGroup);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
}
