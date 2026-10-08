package com.limloch.triage.scoring;

/**
 * One contributing factor to a patient's triage score.
 */
public record TriageFactor(
        String factor,
        String value,
        int weight,
        String reason
) {}