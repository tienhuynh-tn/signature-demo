package com.demo.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import reactor.netty.http.client.HttpClient;

@Configuration
class AiHttpConfig {
    @Bean
    WebClient signatureAiWebClient() {
        HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(8));
        return WebClient.builder()
                .baseUrl("https://signature-matching-prototype.onrender.com")
                .clientConnector(new ReactorClientHttpConnector(http))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
