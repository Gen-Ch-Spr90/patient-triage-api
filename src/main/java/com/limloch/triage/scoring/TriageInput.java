package com.limloch.triage.scoring;

/**
 * Normalized inputs fed into the TriageScorer. All fields optional —
 * missing vitals simply don't contribute to the score.
 */
public record TriageInput(
        String chiefComplaint,
        Double spo2,
        Integer systolic,
        Integer heartRate,
        Integer gcs,
        String mechanismOfInjury,
        boolean pregnant,
        Integer gestationalWeeks,
        boolean fetalMovementDecreased
) {
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String chiefComplaint;
        private Double spo2;
        private Integer systolic;
        private Integer heartRate;
        private Integer gcs;
        private String mechanismOfInjury;
        private boolean pregnant;
        private Integer gestationalWeeks;
        private boolean fetalMovementDecreased;

        public Builder chiefComplaint(String v) { this.chiefComplaint = v; return this; }
        public Builder spo2(Double v) { this.spo2 = v; return this; }
        public Builder systolic(Integer v) { this.systolic = v; return this; }
        public Builder heartRate(Integer v) { this.heartRate = v; return this; }
        public Builder gcs(Integer v) { this.gcs = v; return this; }
        public Builder mechanismOfInjury(String v) { this.mechanismOfInjury = v; return this; }
        public Builder pregnant(boolean v) { this.pregnant = v; return this; }
        public Builder gestationalWeeks(Integer v) { this.gestationalWeeks = v; return this; }
        public Builder fetalMovementDecreased(boolean v) { this.fetalMovementDecreased = v; return this; }

        public TriageInput build() {
            return new TriageInput(chiefComplaint, spo2, systolic, heartRate, gcs,
                    mechanismOfInjury, pregnant, gestationalWeeks, fetalMovementDecreased);
        }
    }
}