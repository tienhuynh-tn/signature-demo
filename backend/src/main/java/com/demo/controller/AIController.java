package com.demo.controller;

import com.demo.dto.SignatureCheckRequest;
import com.demo.dto.SignatureCheckResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/v1/ai")
public class AIController {

    @PostMapping("/signature-check")
    public SignatureCheckResponse checkSimilarity(@RequestBody SignatureCheckRequest request) {
        // Later, compare sourceImage vs targetImage
        double score = 0.5 + new Random().nextDouble() * 0.5; // 0.5 - 1.0
        return new SignatureCheckResponse(score);
    }
}
