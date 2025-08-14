package com.demo.service;

import com.demo.dto.*;
import com.demo.entity.Application;
import com.demo.entity.Application.Status;
import com.demo.entity.User;
import com.demo.repository.ApplicationRepository;
import com.demo.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        zebeeService.sendMessageEvent("Msg_CustomerUpload", app.getId().toString());
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
            return "Approval requested";
        }
        // TODO: Trigger Camunda here
        return "Application not found";
    }

    @Override
    public String review(UUID applicationId, String action) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        app.setStatus(StringUtils.equalsAnyIgnoreCase(action, "approved") ? Status.APPROVED : Status.REJECTED);
        applicationRepository.save(app);

        // TODO: Trigger Camunda here
        zebeeService.sendMessageEvent("Msg_ManagerApproved", app.getId().toString());

        return String.format("Application %s successfully %s", applicationId, action);
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
