package com.ajosavings.ajosavigs.controller;

import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import com.ajosavings.ajosavigs.service.serviceImpl.AjoGroupServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.PersonalSavingsServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.TransactionServiceImpl;
import com.ajosavings.ajosavigs.service.serviceImpl.UsersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/get-all-users")
    public ResponseEntity<Long> getAllUsersNumber(Authentication authentication) {
        return usersService.getAllUsers(authentication);
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

}




