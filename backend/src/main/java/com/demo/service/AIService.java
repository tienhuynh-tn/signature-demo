package com.demo.service;

import com.demo.dto.AIResponse;

import java.io.IOException;
import java.util.UUID;

public interface AIService {
    AIResponse checkSimilarity(UUID applicationId) throws IOException;
}
