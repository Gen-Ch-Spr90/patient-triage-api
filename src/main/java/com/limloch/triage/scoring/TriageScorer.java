package com.limloch.triage.scoring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Rule-based triage scoring engine. Weights and thresholds are simplified
 * versions of published clinical heuristics (ESI, START) for demonstration.
 * Real clinical scoring requires validated protocols and clinical oversight.
 */
@Component
public class TriageScorer {

    public TriageScore score(TriageInput input) {
        List<TriageFactor> factors = new ArrayList<>();
        int total = 0;

        total += scoreOxygen(input, factors);
        total += scoreBloodPressure(input, factors);
        total += scoreHeartRate(input, factors);
        total += scoreGcs(input, factors);
        total += scoreMechanism(input, factors);
        total += scorePregnancy(input, factors);
        total += scoreChiefComplaint(input, factors);

        return TriageScore.of(Math.min(total, 100), factors);
    }

    private int scoreOxygen(TriageInput input, List<TriageFactor> factors) {
        Double spo2 = input.spo2();
        if (spo2 == null) return 0;
        if (spo2 < 85) {
            factors.add(new TriageFactor("SpO2", spo2 + "%", 25, "Severe hypoxia — critical"));
            return 25;
        }
        if (spo2 < 90) {
            factors.add(new TriageFactor("SpO2", spo2 + "%", 20, "Hypoxia below 90%"));
            return 20;
        }
        if (spo2 < 94) {
            factors.add(new TriageFactor("SpO2", spo2 + "%", 8, "Mild hypoxia"));
            return 8;
        }
        return 0;
    }

    private int scoreBloodPressure(TriageInput input, List<TriageFactor> factors) {
        Integer systolic = input.systolic();
        if (systolic == null) return 0;
        if (systolic < 80) {
            factors.add(new TriageFactor("Systolic BP", systolic + " mmHg", 25, "Severe hypotension — shock suspected"));
            return 25;
        }
        if (systolic < 90) {
            factors.add(new TriageFactor("Systolic BP", systolic + " mmHg", 18, "Hypotension"));
            return 18;
        }
        if (systolic > 180) {
            factors.add(new TriageFactor("Systolic BP", systolic + " mmHg", 10, "Hypertensive crisis"));
            return 10;
        }
        return 0;
    }

    private int scoreHeartRate(TriageInput input, List<TriageFactor> factors) {
        Integer hr = input.heartRate();
        if (hr == null) return 0;
        if (hr > 130) {
            factors.add(new TriageFactor("Heart Rate", hr + " bpm", 12, "Severe tachycardia"));
            return 12;
        }
        if (hr > 110) {
            factors.add(new TriageFactor("Heart Rate", hr + " bpm", 8, "Tachycardia"));
            return 8;
        }
        if (hr < 50) {
            factors.add(new TriageFactor("Heart Rate", hr + " bpm", 10, "Bradycardia"));
            return 10;
        }
        return 0;
    }

    private int scoreGcs(TriageInput input, List<TriageFactor> factors) {
        Integer gcs = input.gcs();
        if (gcs == null) return 0;
        if (gcs <= 8) {
            factors.add(new TriageFactor("GCS", String.valueOf(gcs), 25, "Severe neurologic impairment"));
            return 25;
        }
        if (gcs <= 12) {
            factors.add(new TriageFactor("GCS", String.valueOf(gcs), 15, "Moderate neurologic impairment"));
            return 15;
        }
        return 0;
    }

    private int scoreMechanism(TriageInput input, List<TriageFactor> factors) {
        String mechanism = input.mechanismOfInjury();
        if (mechanism == null) return 0;
        String lower = mechanism.toLowerCase();
        if (lower.contains("fire") || lower.contains("burn")
                || lower.contains("high-speed") || lower.contains("mvc")) {
            factors.add(new TriageFactor("Mechanism", mechanism, 15, "High-energy trauma mechanism"));
            return 15;
        }
        return 0;
    }

    private int scorePregnancy(TriageInput input, List<TriageFactor> factors) {
        if (!input.pregnant()) return 0;
        Integer weeks = input.gestationalWeeks();
        int weeksValue = weeks == null ? 0 : weeks;
        if (input.fetalMovementDecreased()) {
            factors.add(new TriageFactor("Pregnancy",
                    weeksValue + " weeks, decreased fetal movement",
                    45,
                    "Decreased fetal movement in viable pregnancy — obstetric emergency"));
            return 45;
        }
        if (weeksValue >= 20) {
            factors.add(new TriageFactor("Pregnancy", weeksValue + " weeks",
                    15, "Pregnancy at viable gestational age — escalate"));
            return 15;
        }
        return 5;
    }

    private int scoreChiefComplaint(TriageInput input, List<TriageFactor> factors) {
        String complaint = input.chiefComplaint();
        if (complaint == null) return 0;
        String lower = complaint.toLowerCase();
        int points = 0;
        if (lower.contains("chest pain") || lower.contains("resuscitation")) {
            points += 15;
            factors.add(new TriageFactor("Chief Complaint", complaint, 15, "Chest pain or resuscitation keywords"));
        } else if (lower.contains("shortness of breath") || lower.contains("dyspnea")) {
            points += 12;
            factors.add(new TriageFactor("Chief Complaint", complaint, 12, "Respiratory complaint"));
        } else if (lower.contains("abdominal pain") || lower.contains("fetal movement")) {
            points += 15;
            factors.add(new TriageFactor("Chief Complaint", complaint, 15, "Abdominal pain or fetal concern"));
        } else if (lower.contains("fatigue")) {
            points += 3;
            factors.add(new TriageFactor("Chief Complaint", complaint, 3, "Chronic fatigue — low acuity"));
        } else if (lower.contains("screening") || lower.contains("routine")) {
            factors.add(new TriageFactor("Chief Complaint", complaint, 0, "Routine or screening visit"));
        }
        return points;
    }
}