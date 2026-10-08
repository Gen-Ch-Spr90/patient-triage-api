// Program Name: patient-triage-api
// Where & When: [Fill in your location and date]
// Who Built: [Fill in your name]
// Build Date: [Fill in exact build date]

// Brief Description: This program defines a Patient model used by the Spring
// Boot REST API. Each patient has an ID, name, and urgency level.

// The program also uses Jakarta Validation annotations so that invalid data is
// rejected at the API boundary before it reaches the service layer.

// The program also provides getters and setters so Spring can serialize the
// object to JSON and deserialize incoming requests.

// The program contains these classes: Patient.

// Comments are left to provide understanding of what each class, method and
// variable represents in this program.

package com.limlochvera.triage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Patient {

    @NotBlank(message = "id is required")
    private String id;

    @NotBlank(message = "name is required")
    private String name;

    @Min(value = 0, message = "urgency must be zero or greater")
    private int urgency;

    public Patient() { }

    public Patient(String id, String name, int urgency) {
        this.id = id;
        this.name = name;
        this.urgency = urgency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }
}