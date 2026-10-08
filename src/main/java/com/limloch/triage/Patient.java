package com.limloch.triage;

import com.limloch.triage.scoring.TriageScore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Patient {

    @NotBlank(message = "id is required")
    private String id;

    @NotBlank(message = "name is required")
    private String name;

    @Min(value = 0, message = "urgency must be zero or greater")
    private int urgency;

    // Optional clinical inputs. When present, they drive the TriageScorer.
    private String chiefComplaint;
    private Double spo2;
    private Integer systolic;
    private Integer heartRate;
    private Integer gcs;
    private String mechanismOfInjury;
    private boolean pregnant;
    private Integer gestationalWeeks;
    private boolean fetalMovementDecreased;

    // Populated by PatientTriageService after scoring. Read-only from the API.
    private TriageScore triageScore;

    public Patient() { }

    public Patient(String id, String name, int urgency) {
        this.id = id;
        this.name = name;
        this.urgency = urgency;
    }

    /**
     * Priority used by the triage queue. If the patient has been scored,
     * returns the computed score. Otherwise falls back to the legacy urgency.
     */
    public int effectiveUrgency() {
        return triageScore != null ? triageScore.total() : urgency;
    }
    /**
     * True when the patient has at least one clinical field populated.
     * Legacy patients constructed with only (id, name, urgency) return false
     * and are never scored — their raw urgency is used instead.
     */
    public boolean hasClinicalInputs() {
        return chiefComplaint != null
                || spo2 != null
                || systolic != null
                || heartRate != null
                || gcs != null
                || mechanismOfInjury != null
                || pregnant
                || gestationalWeeks != null
                || fetalMovementDecreased;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public Double getSpo2() { return spo2; }
    public void setSpo2(Double spo2) { this.spo2 = spo2; }

    public Integer getSystolic() { return systolic; }
    public void setSystolic(Integer systolic) { this.systolic = systolic; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getGcs() { return gcs; }
    public void setGcs(Integer gcs) { this.gcs = gcs; }

    public String getMechanismOfInjury() { return mechanismOfInjury; }
    public void setMechanismOfInjury(String mechanismOfInjury) { this.mechanismOfInjury = mechanismOfInjury; }

    public boolean isPregnant() { return pregnant; }
    public void setPregnant(boolean pregnant) { this.pregnant = pregnant; }

    public Integer getGestationalWeeks() { return gestationalWeeks; }
    public void setGestationalWeeks(Integer gestationalWeeks) { this.gestationalWeeks = gestationalWeeks; }

    public boolean isFetalMovementDecreased() { return fetalMovementDecreased; }
    public void setFetalMovementDecreased(boolean fetalMovementDecreased) { this.fetalMovementDecreased = fetalMovementDecreased; }

    public TriageScore getTriageScore() { return triageScore; }
    public void setTriageScore(TriageScore triageScore) { this.triageScore = triageScore; }
}