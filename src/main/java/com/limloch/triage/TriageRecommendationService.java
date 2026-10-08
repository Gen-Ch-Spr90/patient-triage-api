package com.limloch.triage;

import com.limloch.triage.guidance.ClinicalGuidance;
import com.limloch.triage.guidance.ClinicalGuidanceClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the two-service flow: rank patients, then fetch clinical
 * guidance for the top-ranked patient from the RAG platform.
 */
@Service
public class TriageRecommendationService {

    private final PatientTriageService triageService;
    private final ClinicalGuidanceClient guidanceClient;

    public TriageRecommendationService(PatientTriageService triageService,
                                       ClinicalGuidanceClient guidanceClient) {
        this.triageService = triageService;
        this.guidanceClient = guidanceClient;
    }

    public TriageRecommendation recommend(List<String> patientIds) {
        List<Patient> ranked = triageService.compare(patientIds);
        if (ranked.isEmpty()) {
            return new TriageRecommendation(List.of(), null);
        }
        Patient top = ranked.get(0);
        ClinicalGuidance guidance = guidanceClient.fetch(buildQuestion(top), 3);
        return new TriageRecommendation(ranked, guidance);
    }

    private String buildQuestion(Patient top) {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient with ");

        String complaint = top.getChiefComplaint();
        sb.append(complaint == null || complaint.isBlank() ? "unspecified presentation" : complaint);

        if (top.getSpo2() != null) sb.append(", SpO2 ").append(top.getSpo2()).append("%");
        if (top.getSystolic() != null) sb.append(", systolic BP ").append(top.getSystolic()).append(" mmHg");
        if (top.getHeartRate() != null) sb.append(", HR ").append(top.getHeartRate());
        if (top.getGcs() != null) sb.append(", GCS ").append(top.getGcs());
        if (top.getMechanismOfInjury() != null) sb.append(", mechanism: ").append(top.getMechanismOfInjury());

        sb.append(". What is the immediate clinical management?");
        return sb.toString();
    }
}