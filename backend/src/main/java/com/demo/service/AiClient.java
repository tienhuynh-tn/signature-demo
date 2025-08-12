package com.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
class AiClient {
    private final WebClient signatureAiWebClient;

    // Request wrapper matches provider shape
    record ProviderReq(Inner signature_request) { record Inner(String sourceImage, String targetImage) {} }

    double verify(byte[] sourceImage, byte[] targetImage) {
        String sourceBase64 = Base64.getEncoder().encodeToString(sourceImage);
        String targetBase64 = Base64.getEncoder().encodeToString(targetImage);

        ProviderReq body = new ProviderReq(new ProviderReq.Inner(sourceBase64, targetBase64));

        Map<String, Object> res = signatureAiWebClient.post()
                .uri("/verify")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(b -> Mono.error(new IllegalStateException("AI verify failed: " + b))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (res == null) throw new IllegalStateException("Empty response from AI service");
        Object val = res.get("score");
        if (val == null) val = res.get("similarity"); // tolerate alt key
        if (val == null) throw new IllegalStateException("Missing score/similarity in AI response: " + res);

        return ((Number) val).doubleValue();
    }
}
