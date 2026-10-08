package com.limloch.triage;

import com.limloch.triage.scoring.TriageScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TriageComparisonTest {

    private PatientTriageService service;

    @BeforeEach
    void setUp() {
        service = new PatientTriageService(new TriageScorer());
    }

    private Patient traumaPatient() {
        Patient p = new Patient();
        p.setId("T1");
        p.setName("Trauma Patient");
        p.setChiefComplaint("Chest pain and respiratory failure");
        p.setSpo2(89.0);
        p.setSystolic(78);
        p.setHeartRate(138);
        p.setGcs(6);
        p.setMechanismOfInjury("High-speed MVC with vehicle fire");
        return p;
    }

    private Patient obstetricPatient() {
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
        return p;
    }

    private Patient routinePatient() {
        Patient p = new Patient();
        p.setId("R1");
        p.setName("Routine Patient");
        p.setChiefComplaint("Routine STI screening");
        p.setSpo2(99.0);
        p.setSystolic(118);
        p.setHeartRate(72);
        return p;
    }

    @Test
    void compare_ranksPatientsByUrgency() {
        service.addPatient(routinePatient());
        service.addPatient(obstetricPatient());
        service.addPatient(traumaPatient());

        List<Patient> ranked = service.compare(List.of("R1", "OB1", "T1"));

        assertEquals(3, ranked.size());
        assertEquals("T1", ranked.get(0).getId(), "Trauma should rank #1");
        assertEquals("OB1", ranked.get(1).getId(), "Obstetric should rank #2");
        assertEquals("R1", ranked.get(2).getId(), "Routine should rank #3");
    }

    @Test
    void compare_skipsUnknownIds() {
        service.addPatient(traumaPatient());

        List<Patient> ranked = service.compare(List.of("T1", "DOES_NOT_EXIST"));

        assertEquals(1, ranked.size());
        assertEquals("T1", ranked.get(0).getId());
    }

    @Test
    void compare_isReadOnly_doesNotDrainQueue() {
        service.addPatient(traumaPatient());
        service.addPatient(routinePatient());

        int before = service.remainingCount();
        service.compare(List.of("T1", "R1"));
        int after = service.remainingCount();

        assertEquals(before, after, "compare must not modify the queue");
    }
}