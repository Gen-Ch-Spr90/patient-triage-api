package com.limloch.triage;

import com.limloch.triage.guidance.ClinicalGuidance;

import java.util.List;

/**
 * Combined result: patients ranked by severity plus clinical guidance for
 * the top-ranked patient, sourced from the RAG platform.
 */
public record TriageRecommendation(
        List<Patient> ranked,
        ClinicalGuidance guidance
) {}