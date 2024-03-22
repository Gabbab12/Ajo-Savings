package com.ajosavings.ajosavigs.dto.response;

import com.ajosavings.ajosavigs.models.PersonalSavings;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPage {
    private List<PersonalSavings> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
