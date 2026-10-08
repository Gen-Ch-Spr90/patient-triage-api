package com.limloch.triage.scoring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriageScorerTest {

    private final TriageScorer scorer = new TriageScorer();

    @Test
    void traumaPatientScoresImmediate() {
        TriageScore score = scorer.score(TriageInput.builder()
                .chiefComplaint("Chest pain and respiratory failure")
                .spo2(89.0)
                .systolic(78)
                .heartRate(138)
                .gcs(6)
                .mechanismOfInjury("High-speed MVC with vehicle fire")
                .build());

        assertThat(score.tier()).isEqualTo(SeverityTier.IMMEDIATE);
        assertThat(score.total()).isGreaterThanOrEqualTo(80);
        assertThat(score.factors()).isNotEmpty();
    }

    @Test
    void obstetricEmergencyScoresEmergent() {
        TriageScore score = scorer.score(TriageInput.builder()
                .chiefComplaint("Abdominal pain, decreased fetal movement")
                .spo2(97.0)
                .systolic(138)
                .heartRate(104)
                .pregnant(true)
                .gestationalWeeks(32)
                .fetalMovementDecreased(true)
                .build());

        assertThat(score.tier()).isIn(SeverityTier.EMERGENT, SeverityTier.IMMEDIATE);
        assertThat(score.total()).isGreaterThanOrEqualTo(55);
    }

    @Test
    void routineScreeningScoresNonUrgent() {
        TriageScore score = scorer.score(TriageInput.builder()
                .chiefComplaint("Routine STI screening")
                .spo2(99.0)
                .systolic(118)
                .heartRate(72)
                .build());

        assertThat(score.tier()).isEqualTo(SeverityTier.NON_URGENT);
        assertThat(score.total()).isLessThan(30);
    }

    @Test
    void chronicFatigueScoresLow() {
        TriageScore score = scorer.score(TriageInput.builder()
                .chiefComplaint("Chronic fatigue, work-related")
                .spo2(98.0)
                .systolic(132)
                .heartRate(78)
                .build());

        assertThat(score.tier()).isEqualTo(SeverityTier.NON_URGENT);
    }

    @Test
    void explanationIncludesAllAppliedFactors() {
        TriageScore score = scorer.score(TriageInput.builder()
                .spo2(85.0)
                .systolic(75)
                .gcs(7)
                .build());

        assertThat(score.factors()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(score.factors())
                .extracting(TriageFactor::factor)
                .contains("SpO2", "Systolic BP", "GCS");
    }
}