package com.limloch.triage;

import com.limloch.triage.guidance.ClinicalGuidance;
import com.limloch.triage.guidance.ClinicalGuidanceClient;
import com.limloch.triage.scoring.TriageScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TriageRecommendationServiceTest {

    /** Fake RAG client that records the last question and returns canned guidance. */
    static class FakeClinicalGuidanceClient implements ClinicalGuidanceClient {
        final List<String> questionsSeen = new ArrayList<>();

        @Override
        public ClinicalGuidance fetch(String question, int topK) {
            questionsSeen.add(question);
            return new ClinicalGuidance(
                    "Activate trauma bay and massive transfusion protocol.",
                    List.of(new ClinicalGuidance.Source(1, "Trauma Activation Note", 0, 0.44)),
                    new ClinicalGuidance.Metrics(1264, 78, 2336, 4, "openai:text-embedding-3-small", "openai:gpt-4o-mini"));
        }
    }

    private FakeClinicalGuidanceClient fakeClient;
    private TriageRecommendationService service;

    @BeforeEach
    void setUp() {
        fakeClient = new FakeClinicalGuidanceClient();
        service = new TriageRecommendationService(
                new PatientTriageService(new TriageScorer()),
                fakeClient);
    }

    private Patient trauma() {
        Patient p = new Patient();
        p.setId("T1");
        p.setName("Trauma");
        p.setChiefComplaint("Chest pain and respiratory failure");
        p.setSpo2(85.0);
        p.setSystolic(75);
        p.setGcs(7);
        p.setMechanismOfInjury("MVC with fire");
        return p;
    }

    private Patient routine() {
        Patient p = new Patient();
        p.setId("R1");
        p.setName("Routine");
        p.setChiefComplaint("Routine screening");
        p.setSpo2(99.0);
        p.setSystolic(118);
        p.setHeartRate(72);
        return p;
    }

    @Test
    void recommend_returnsRankedPatientsAndGuidance() {
        PatientTriageService triageService = new PatientTriageService(new TriageScorer());
        triageService.addPatient(trauma());
        triageService.addPatient(routine());

        // Build a fresh service wired to the same triageService
        TriageRecommendationService svc = new TriageRecommendationService(triageService, fakeClient);
        TriageRecommendation rec = svc.recommend(List.of("T1", "R1"));

        assertEquals(2, rec.ranked().size());
        assertEquals("T1", rec.ranked().get(0).getId(), "Trauma should rank #1");
        assertNotNull(rec.guidance());
        assertEquals("Activate trauma bay and massive transfusion protocol.", rec.guidance().answer());
        assertEquals(1, fakeClient.questionsSeen.size(), "RAG should be called exactly once");
    }

    @Test
    void recommend_sendsTopPatientComplaintToRag() {
        PatientTriageService triageService = new PatientTriageService(new TriageScorer());
        triageService.addPatient(trauma());
        triageService.addPatient(routine());

        TriageRecommendationService svc = new TriageRecommendationService(triageService, fakeClient);
        svc.recommend(List.of("T1", "R1"));

        assertEquals(1, fakeClient.questionsSeen.size());
        String question = fakeClient.questionsSeen.get(0);
        assertTrue(question.contains("Chest pain and respiratory failure"),
                "RAG question should include the trauma patient's complaint: " + question);
    }

    @Test
    void recommend_emptyList_returnsNoGuidanceAndNoRagCall() {
        TriageRecommendation rec = service.recommend(List.of());

        assertTrue(rec.ranked().isEmpty());
        assertNull(rec.guidance());
        assertTrue(fakeClient.questionsSeen.isEmpty(), "RAG should not be called for empty input");
    }
}