package com.ajosavings.ajosavigs.dto.request;

import com.ajosavings.ajosavigs.enums.PaymentPeriod;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class AjoGroupDTO {
    private String groupName;
    private double contributionAmount;
    private Date expectedStartDate;
    private Date expectedEndDate;
    private String duration;
    private int numberOfParticipant;
    private PaymentPeriod paymentPeriod;
    private LocalTime time;
    private String purposeAndGoals;

}
