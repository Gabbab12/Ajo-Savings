package com.ajosavings.ajosavigs.dto.response;

import com.ajosavings.ajosavigs.models.GroupTransactionHistory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupTransactionPage {
    private List<GroupTransactionHistory> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
