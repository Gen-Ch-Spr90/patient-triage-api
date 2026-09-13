// Program Name: patient-triage-api
// Where & When: [Fill in your location and date]
// Who Built: [Fill in your name]
// Build Date: [Fill in exact build date]

// Brief Description: This program contains JUnit 5 tests for the
// PatientTriageService. It verifies that patients are stored, retrieved,
// and returned in urgency order.

// The program also tests the empty-queue case so the service can safely return
// null when no patients remain.

// The program also confirms the remaining count decreases as patients are
// processed.

// The program contains these classes: PatientTriageServiceTest.

// Comments are left to provide understanding of what each class, method and
// variable represents in this program.

package com.limlochvera.triage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientTriageServiceTest {

    private PatientTriageService service;

    @BeforeEach
    void setUp() {
        service = new PatientTriageService();
    }

    @Test
    void addPatient_shouldStorePatient() {
        service.addPatient(new Patient("P1", "Alice", 2));
        assertEquals(1, service.remainingCount());
        assertNotNull(service.getPatientById("P1"));
    }

    @Test
    void getNextPatient_shouldReturnHighestUrgencyFirst() {
        service.addPatient(new Patient("P1", "Alice", 2));
        service.addPatient(new Patient("P2", "Bob", 9));
        service.addPatient(new Patient("P3", "Charlie", 5));

        Patient next = service.getNextPatient();
        assertEquals("P2", next.getId());
        assertEquals(9, next.getUrgency());
    }

    @Test
    void getNextPatient_shouldReturnNullWhenEmpty() {
        assertNull(service.getNextPatient());
    }

    @Test
    void remainingCount_shouldDecreaseAfterProcessing() {
        service.addPatient(new Patient("P1", "Alice", 2));
        service.addPatient(new Patient("P2", "Bob", 9));

        service.getNextPatient();
        assertEquals(1, service.remainingCount());
    }
}