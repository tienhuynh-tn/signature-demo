package com.demo.service;

import com.demo.dto.*;
import com.demo.entity.Application;
import com.demo.entity.Application.Status;
import com.demo.entity.User;
import com.demo.repository.ApplicationRepository;
import com.demo.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final ApplicationRepository applicationRepository;
    private final ConfigRepository configRepository;
    private final RestTemplate restTemplate;
    private final ZebeeService zebeeService;

    @Override
    public String uploadSignature(SignatureUploadRequest request) {
        Application app = new Application();
        app.setSignatureImage(request.getImageBase64());
        app.setUser(User.builder().id(request.getUserId()).build());
        app.setStatus(Status.CREATED);
        applicationRepository.save(app);

        // TODO: Trigger Camunda here
        zebeeService.startNewInstance("Start", app.getId().toString());

        return "Uploaded";
    }

    @Override
    public SignatureResponse getSignatureDetails(UUID applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        User user = app.getUser();

        return SignatureResponse.builder()
                .applicationId(app.getId().toString())
                .userId(user != null ? user.getId().toString() : null)
                .fullName(user != null ? user.getFullName() : null)
                .signatureImage(app.getSignatureImage())
                .status(String.valueOf(app.getStatus()))
                .createdDate(app.getCreatedDate())
                .build();
    }

    @Override
    public String requestApproval(UUID applicationId) {
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            app.setStatus(Status.PENDING);
            applicationRepository.save(app);
            // TODO: Trigger Camunda here
            zebeeService.sendMessageEvent("Msg_StaffReviewed", app.getId().toString());
            return "Approval requested";
        }
        // TODO: Trigger Camunda here
        return "Application not found";
    }

    @Override
    public Object approveRequest(UUID applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        SignatureCheckRequest aiRequest = new SignatureCheckRequest(app.getSignatureImage(), "target-image"); // can be fixed or dynamic

        String aiUrl = "http://localhost:8081/v1/ai/signature-check";
        SignatureCheckResponse aiResponse = restTemplate.postForObject(aiUrl, aiRequest, SignatureCheckResponse.class);
        if (aiResponse == null) throw new IllegalStateException("AI service failed");

        double score = aiResponse.getScore();

        double threshold = configRepository.findAll().stream()
                .filter(c -> "SIGNATURE_THRESHOLD".equals(c.getCode()))
                .findFirst()
                .map(c -> Double.parseDouble(c.getValue()))
                .orElse(0.8);

        boolean isMatch = score >= threshold;
        app.setStatus(isMatch ? Status.APPROVED : Status.REJECTED);
        applicationRepository.save(app);

        // TODO: Send response to Camunda

        return new AIResult(isMatch ? "match" : "not match", score);
    }

    @Override
    public List<SignatureResponse> getAllSignatures(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        List<Application> applications = applicationRepository.findAllWithUsers();

        // Combine data from Application and UserRepository
        List<SignatureResponse> allResponses = applications.stream()
                .map(app -> SignatureResponse.builder()
                        .applicationId(app.getId().toString())
                        .userId(app.getUser().getId().toString())
                        .fullName(app.getUser().getFullName())
                        .signatureImage(app.getSignatureImage())
                        .status(String.valueOf(app.getStatus()))
                        .createdDate(app.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        // Safe pagination
        int fromIndex = page * size;
        if (fromIndex >= allResponses.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + size, allResponses.size());
        return allResponses.subList(fromIndex, toIndex);
    }
}
