package com.limloch.triage.scoring;

import java.util.List;

/**
 * Complete result of scoring a patient: total, derived tier, and the
 * contributing factors so the decision is fully explainable.
 */
public record TriageScore(
        int total,
        SeverityTier tier,
        List<TriageFactor> factors
) {
    public TriageScore {
        factors = List.copyOf(factors);
    }

    public static TriageScore of(int total, List<TriageFactor> factors) {
        return new TriageScore(total, SeverityTier.fromScore(total), factors);
    }
}