// Program Name: patient-triage-api
// Where & When: [Fill in your location and date]
// Who Built: [Fill in your name]
// Build Date: [Fill in exact build date]

// Brief Description: This program defines a service class that stores patients
// in memory and returns the highest-urgency patient first.

// The program also uses a PriorityQueue ordered by urgency, so the service
// always pops the most urgent patient without sorting the whole list.

// The program also keeps a HashMap for quick lookup by patient ID, which
// mirrors how a real service would use a database index.

// The program contains these classes: PatientTriageService.

// Comments are left to provide understanding of what each class, method and
// variable represents in this program.

package com.limlochvera.triage;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class PatientTriageService {

    private final Map<String, Patient> patientsById = new HashMap<>();

    private final PriorityQueue<Patient> queue =
        new PriorityQueue<>((a, b) -> Integer.compare(b.getUrgency(), a.getUrgency()));

    public Patient addPatient(Patient patient) {
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

    public List<Patient> listAll() {
        return new ArrayList<>(patientsById.values());
    }

    public int remainingCount() {
        return queue.size();
    }
}