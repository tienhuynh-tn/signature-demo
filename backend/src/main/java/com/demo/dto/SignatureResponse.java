package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureResponse {
    private String applicationId;
    private String userId;
    private String fullName;
    private String signatureImage;
    private String status; // e.g., "PENDING", "APPROVED", "REJECTED"
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
