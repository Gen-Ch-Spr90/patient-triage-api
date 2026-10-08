package com.limloch.triage;

import com.limloch.triage.scoring.TriageScore;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}/triage-explanation")
    public ResponseEntity<Map<String, Object>> triageExplanation(@PathVariable String id) {
        TriageScore score = service.getTriageScore(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "patientId", id,
                "total", score.total(),
                "tier", score.tier().name(),
                "tierDescription", score.tier().description(),
                "factors", score.factors()
        ));
    }
}