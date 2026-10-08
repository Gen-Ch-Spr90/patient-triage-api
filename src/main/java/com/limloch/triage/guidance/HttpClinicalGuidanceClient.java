package com.limloch.triage.guidance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Real HTTP client that calls the RAG platform's POST /api/query endpoint.
 * Base URL comes from rag.base-url in application.properties.
 */
@Component
public class HttpClinicalGuidanceClient implements ClinicalGuidanceClient {

    private final RestClient client;

    public HttpClinicalGuidanceClient(@Value("${rag.base-url}") String baseUrl) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ClinicalGuidance fetch(String question, int topK) {
        return client.post()
                .uri("/api/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("question", question, "topK", topK))
                .retrieve()
                .body(ClinicalGuidance.class);
    }
}