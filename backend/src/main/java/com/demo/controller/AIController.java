package com.demo.controller;

import com.demo.dto.AIRequest;
import com.demo.dto.AIResponse;
import com.demo.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/signature-check")
    public AIResponse checkSimilarity(@RequestBody @Valid AIRequest request) throws IOException {
        return aiService.checkSimilarity(request.getApplicationId());
    }
}
