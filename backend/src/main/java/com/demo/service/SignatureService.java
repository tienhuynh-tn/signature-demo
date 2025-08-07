package com.demo.service;

import com.demo.dto.SignatureResponse;
import com.demo.dto.SignatureUploadRequest;

import java.util.List;
import java.util.UUID;

public interface SignatureService {
    String uploadSignature(SignatureUploadRequest request);
    Object getSignatureDetails(UUID applicationId);
    String requestApproval(UUID applicationId);
    Object approveRequest(UUID applicationId);
    List<SignatureResponse> getAllSignatures(int page, int size);
}
