package com.limloch.triage;

import com.limloch.triage.scoring.TriageInput;
import com.limloch.triage.scoring.TriageScore;
import com.limloch.triage.scoring.TriageScorer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class PatientTriageService {

    private final TriageScorer scorer;
    private final Map<String, Patient> patientsById = new HashMap<>();
    private final PriorityQueue<Patient> queue =
            new PriorityQueue<>((a, b) -> Integer.compare(b.effectiveUrgency(), a.effectiveUrgency()));

    public PatientTriageService(TriageScorer scorer) {
        this.scorer = scorer;
    }

    public Patient addPatient(Patient patient) {
        if (patient.hasClinicalInputs()) {
            TriageScore score = scorer.score(toTriageInput(patient));
            patient.setTriageScore(score);
        }
        patientsById.put(patient.getId(), patient);
        queue.add(patient);
        return patient;
    }

    public Patient getNextPatient() {
        Patient next = queue.poll();
        if (next != null) {
            patientsById.remove(next.getId());
        }
        return next;
    }

    public Patient getPatientById(String id) {
        return patientsById.get(id);
    }

    public TriageScore getTriageScore(String id) {
        Patient p = patientsById.get(id);
        return p == null ? null : p.getTriageScore();
    }
    /**
     * Returns the given patients ranked by effective urgency (highest first),
     * with per-factor explanations included so a reviewer can see why each
     * patient landed where they did.
     */
    public List<Patient> compare(List<String> ids) {
        return ids.stream()
                .map(patientsById::get)
                .filter(java.util.Objects::nonNull)
                .sorted((a, b) -> Integer.compare(b.effectiveUrgency(), a.effectiveUrgency()))
                .toList();
    }

    public List<Patient> listAll() {
        return new ArrayList<>(patientsById.values());
    }

    public int remainingCount() {
        return queue.size();
    }

    private TriageInput toTriageInput(Patient p) {
        return TriageInput.builder()
                .chiefComplaint(p.getChiefComplaint())
                .spo2(p.getSpo2())
                .systolic(p.getSystolic())
                .heartRate(p.getHeartRate())
                .gcs(p.getGcs())
                .mechanismOfInjury(p.getMechanismOfInjury())
                .pregnant(p.isPregnant())
                .gestationalWeeks(p.getGestationalWeeks())
                .fetalMovementDecreased(p.isFetalMovementDecreased())
                .build();
    }
}