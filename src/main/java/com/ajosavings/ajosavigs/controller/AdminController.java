package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.exception.BadRequestException;
import com.ajosavings.ajosavigs.models.AjoGroup;
import com.ajosavings.ajosavigs.models.DefaultedUsers;
import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import com.ajosavings.ajosavigs.models.Users;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.TransactionServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.UsersServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final PersonalSavingsServiceImpl savingsService;
    private final AjoGroupServiceImpl ajoGroupService;
    private final TransactionServiceImpl transactionService;
    private final UsersServiceImpl usersService;


    @GetMapping("/total-amount-saved")
    public ResponseEntity<Double> getTotalAmountSaved(Authentication authentication) {
        return savingsService.getTotalAmountSaved(authentication);
    }

    @GetMapping("/total-saved-groups")
    public ResponseEntity<Long> getTotalSavingGroups() {
        return ajoGroupService.getTotalSavingGroups();
    }

    @GetMapping("/total-amount-withdrawn")
    public ResponseEntity<Double> getTotalAmountWithdrawn(Authentication authentication) {
        return transactionService.getTotalAmountWithdrawn(authentication);
    }

    @GetMapping("/total-contributions")
    public ResponseEntity<Double> getTotalContributions(Authentication authentication) {
        return ajoGroupService.getTotalContributions(authentication);
    }

    @GetMapping("/get-total-users-number")
    public ResponseEntity<Long> getAllUsersNumber(Authentication authentication) {
        return usersService.getTotalNumberOfUsers(authentication);
    }

    @GetMapping("/get-new-created-group")
    public ResponseEntity<Long> getNewGroupNumbers(Authentication authentication) {
        return ajoGroupService.getNewAjoGroups(authentication);
    }

    @GetMapping("/get-received-transactions/{groupId}")
    public ResponseEntity<Page<GroupTransactionHistory>> getGroupReceivedTransactions(@PathVariable Long groupId,
                                                                                      @PageableDefault(size = 10, page = 0) Pageable pageable, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    Page<GroupTransactionHistory> transactionHistoryPage = ajoGroupService.getGroupReceivedTransactions(groupId, pageable);
                    return ResponseEntity.ok(transactionHistoryPage);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/total-new-users")
    public ResponseEntity<Long> getTotalNewUsers(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    long totalNewUsers = usersService.countNewUsers();
                    {
                        return ResponseEntity.ok(totalNewUsers);
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/get-sent-transactions/{groupId}")
    public ResponseEntity<Page<GroupTransactionHistory>> getGroupSentTransactions(@PathVariable Long groupId,
                                                                                  @PageableDefault(size = 10, page = 0) Pageable pageable, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    Page<GroupTransactionHistory> transactionHistoryPage = ajoGroupService.getGroupSentTransactions(groupId, pageable);
                    return ResponseEntity.ok(transactionHistoryPage);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @GetMapping("/get-all-users")
    public ResponseEntity<Page<Users>> getAllUsers(@PageableDefault(size = 15, page = 0) Pageable pageable){
        Page<Users> usersPage = usersService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }
    @GetMapping("get-all-active-users")
    public ResponseEntity<Page<Users>> getAllActiveUsers(@PageableDefault(page = 0, size = 15) Pageable pageable){
        Page<Users> activeUsers = usersService.getAllActiveUsers(pageable);
        return ResponseEntity.ok(activeUsers);
    }
    @GetMapping("/get-new-users")
    public ResponseEntity<Page<Users>> getAllNewUsers(@PageableDefault(page = 0, size = 15) Pageable pageable){
        Page<Users> newUsers = usersService.getAllNewUsers(pageable);
        return ResponseEntity.ok(newUsers);
    }

    @GetMapping("/defaultedUsers")
    public ResponseEntity<Page<DefaultedUsers>> getAllDefaultedUsers(Pageable pageable) {
        try {
            Page<DefaultedUsers> defaultedUsersPage = usersService.getAllDefaultedUsers(pageable);
            return ResponseEntity.ok().body(defaultedUsersPage);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/defaultedUsers/count")
    public ResponseEntity<Long> countNewlyDefaultingUsers() {
        try {
            long count = usersService.countNewlyDefaultingUsers();
            return ResponseEntity.ok().body(count);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/enable-group/{ajoGroupId}")
    public ResponseEntity<AjoGroup> enableAjoGroup(@PathVariable Long ajoGroupId) {
        return ajoGroupService.enableAjoGroup(ajoGroupId);
    }


}




