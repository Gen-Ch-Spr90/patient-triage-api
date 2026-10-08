// Program Name: patient-triage-api
// Where & When: [Fill in your location and date]
// Who Built: [Fill in your name]
// Build Date: [Fill in exact build date]

// Brief Description: This program exposes the PatientTriageService as a REST
// API using Spring Web. It handles creating patients, listing them, fetching
// the next patient, and returning the remaining count.

// The program also uses constructor injection so the controller depends on an
// abstraction rather than creating the service itself.

// The program also returns standard HTTP status codes (200, 201, 404) so
// clients can respond appropriately.

// The program contains these classes: PatientTriageController.

// Comments are left to provide understanding of what each class, method and
// variable represents in this program.

package com.limlochvera.triage;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientTriageController {

    private final PatientTriageService service;

    public PatientTriageController(PatientTriageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patient) {
        return ResponseEntity.status(201).body(service.addPatient(patient));
    }

    @GetMapping
    public List<Patient> listAll() {
        return service.listAll();
    }

    @GetMapping("/next")
    public ResponseEntity<Patient> nextPatient() {
        Patient next = service.getNextPatient();
        if (next == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(next);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable String id) {
        Patient patient = service.getPatientById(id);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/count")
    public int remainingCount() {
        return service.remainingCount();
    }
}