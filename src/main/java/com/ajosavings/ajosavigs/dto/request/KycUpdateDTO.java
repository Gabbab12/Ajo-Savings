package com.ajosavings.ajosavigs.dto.request;

import com.ajosavings.ajosavigs.enums.Gender;
import com.ajosavings.ajosavigs.enums.IdentificationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycUpdateDTO {
    private Gender gender;
    private String occupation;
    private Date dateOfBirth;
    private IdentificationType identificationType;
    private long bvn;
    private String address;
    private String identificationNumber;
    private String uploadIdentificationDocument;
    private String uploadProofOfAddress;
}
