package com.demo.controller;

import com.demo.dto.SignatureResponse;
import com.demo.dto.SignatureUploadRequest;
import com.demo.service.SignatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSignature(@RequestBody SignatureUploadRequest request) {
        return ResponseEntity.ok(signatureService.uploadSignature(request));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getSignatureDetails(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(signatureService.getSignatureDetails(applicationId));
    }

    @PostMapping("/{applicationId}/request-approval")
    public ResponseEntity<?> requestApproval(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(signatureService.requestApproval(applicationId));
    }

    @PostMapping("/{applicationId}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(signatureService.approveRequest(applicationId));
    }

    @GetMapping
    public ResponseEntity<List<SignatureResponse>> getAllSignatures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(signatureService.getAllSignatures(page, size));
    }

}
