package com.limloch.triage.scoring;

/**
 * Clinical severity tiers, inspired by Emergency Severity Index (ESI) and
 * START triage. Higher ordinal = more urgent.
 */
public enum SeverityTier {

    NON_URGENT(0, "Routine — no acute complaint"),
    URGENT(1, "Needs evaluation within hours"),
    EMERGENT(2, "Needs evaluation within minutes — potential emergency"),
    IMMEDIATE(3, "Life-threatening — requires intervention now");

    private final int rank;
    private final String description;

    SeverityTier(int rank, String description) {
        this.rank = rank;
        this.description = description;
    }

    public int rank() { return rank; }
    public String description() { return description; }

    public static SeverityTier fromScore(int score) {
        if (score >= 80) return IMMEDIATE;
        if (score >= 55) return EMERGENT;
        if (score >= 30) return URGENT;
        return NON_URGENT;
    }
}