package com.limloch.triage;

import com.limloch.triage.scoring.SeverityTier;
import com.limloch.triage.scoring.TriageScore;
import com.limloch.triage.scoring.TriageScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriageExplanationTest {

    private PatientTriageService service;

    @BeforeEach
    void setUp() {
        service = new PatientTriageService(new TriageScorer());
    }

    @Test
    void traumaPatient_scoresImmediate() {
        Patient p = new Patient();
        p.setId("T1");
        p.setName("Trauma Patient");
        p.setChiefComplaint("Chest pain and respiratory failure");
        p.setSpo2(89.0);
        p.setSystolic(78);
        p.setHeartRate(138);
        p.setGcs(6);
        p.setMechanismOfInjury("High-speed MVC with vehicle fire");

        service.addPatient(p);

        TriageScore score = service.getTriageScore("T1");
        assertNotNull(score);
        assertEquals(SeverityTier.IMMEDIATE, score.tier());
        assertTrue(score.factors().size() >= 5,
                "Expected at least 5 contributing factors, got " + score.factors().size());
    }

    @Test
    void obstetricEmergency_scoresEmergent() {
        Patient p = new Patient();
        p.setId("OB1");
        p.setName("Obstetric Patient");
        p.setChiefComplaint("Abdominal pain, decreased fetal movement");
        p.setSpo2(97.0);
        p.setSystolic(138);
        p.setHeartRate(104);
        p.setPregnant(true);
        p.setGestationalWeeks(32);
        p.setFetalMovementDecreased(true);

        service.addPatient(p);

        TriageScore score = service.getTriageScore("OB1");
        assertNotNull(score);
        assertEquals(SeverityTier.EMERGENT, score.tier());
    }

    @Test
    void routineScreening_scoresNonUrgent() {
        Patient p = new Patient();
        p.setId("R1");
        p.setName("Routine Patient");
        p.setChiefComplaint("Routine STI screening");
        p.setSpo2(99.0);
        p.setSystolic(118);
        p.setHeartRate(72);

        service.addPatient(p);

        TriageScore score = service.getTriageScore("R1");
        assertNotNull(score);
        assertEquals(SeverityTier.NON_URGENT, score.tier());
    }

    @Test
    void triageQueue_ordersByClinicalSeverity() {
        Patient routine = new Patient("R1", "Routine", 0);
        routine.setChiefComplaint("Routine screening");
        routine.setSpo2(99.0);
        routine.setSystolic(118);
        routine.setHeartRate(72);

        Patient trauma = new Patient("T1", "Trauma", 0);
        trauma.setChiefComplaint("Chest pain");
        trauma.setSpo2(85.0);
        trauma.setSystolic(75);
        trauma.setGcs(7);
        trauma.setMechanismOfInjury("MVC with fire");

        service.addPatient(routine);
        service.addPatient(trauma);

        Patient next = service.getNextPatient();
        assertEquals("T1", next.getId(), "Trauma patient should be dequeued first");
    }

    @Test
    void legacyPatient_withoutClinicalFields_usesRawUrgency() {
        Patient legacy = new Patient("L1", "Legacy", 42);
        service.addPatient(legacy);

        assertEquals(42, legacy.getUrgency());
        assertEquals(42, legacy.effectiveUrgency());
    }
}