package com.limloch.triage.guidance;

import java.util.List;

/**
 * Response shape returned by the RAG platform's POST /api/query endpoint.
 * Mirrors the JSON contract so Jackson can deserialize directly.
 */
public record ClinicalGuidance(
        String answer,
        List<Source> sources,
        Metrics metrics
) {
    public record Source(
            int citation,
            String documentTitle,
            int chunkIndex,
            double distance
    ) {}

    public record Metrics(
            int promptTokens,
            int completionTokens,
            long llmLatencyMs,
            int retrievedChunks,
            String embeddingProvider,
            String llmProvider
    ) {}
}