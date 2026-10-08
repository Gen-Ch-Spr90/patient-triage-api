package com.limloch.triage.guidance;

/**
 * Abstraction over the RAG platform. Implementations may call the real
 * HTTP endpoint, or return canned data in tests.
 */
public interface ClinicalGuidanceClient {

    /**
     * Fetch clinical guidance for the given question.
     *
     * @param question the natural-language question
     * @param topK     how many source chunks to retrieve
     * @return the guidance with answer, sources, and metrics
     */
    ClinicalGuidance fetch(String question, int topK);
}