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
        String complaint = top.getChiefComplaint();
        if (complaint == null || complaint.isBlank()) {
            complaint = "unspecified presentation";
        }
        return "Given the following chief complaint, what is the recommended immediate clinical action? Chief complaint: " + complaint;
    }
}