package com.demo.service;

import com.demo.dto.AIResponse;
import com.demo.entity.Application;
import com.demo.repository.ApplicationRepository;
import com.demo.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class AIServiceImpl implements AIService {
    private final ApplicationRepository applicationRepository;
    private final AiClient aiClient;
    private final ConfigRepository configRepository;

    @Override
    public AIResponse checkSimilarity(UUID applicationId) throws IOException {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("No signature found for applicationId=" + applicationId));

        if (StringUtils.isBlank(application.getSignatureImage())) {
            throw new IllegalStateException("Both source and target images must be present for applicationId=" + applicationId);
        }

        String base64 = application.getSignatureImage();
        // If the string starts with data URI prefix, remove it
        if (base64.startsWith("data:image")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }
        byte[] targetBytes = Base64.getDecoder().decode(base64);

        ClassPathResource resource = new ClassPathResource("image.txt");
        base64 = Files.readString(Path.of(resource.getURI()));
        if (base64.startsWith("data:image")) {
            base64 = base64.substring(base64.indexOf(",") + 1); // strip data URI prefix
        }
        byte[] imageBytes = Base64.getDecoder().decode(base64);

        double score = aiClient.verify(imageBytes, targetBytes);
        double threshold = configRepository.findAll().stream()
                .filter(c -> "SIGNATURE_THRESHOLD".equals(c.getCode()))
                .findFirst()
                .map(c -> Double.parseDouble(c.getValue()))
                .orElse(0.8);

        boolean isMatch = score >= threshold;

        return new AIResponse(isMatch ? "isMatch" : "isNotMatch", score);
    }
}
