package com.limloch.triage;

import com.limloch.triage.scoring.TriageScore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/triage")
public class TriageCompareController {

    private final PatientTriageService service;

    public TriageCompareController(PatientTriageService service) {
        this.service = service;
    }

    public record CompareRequest(@NotEmpty List<String> patientIds) {}

    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compare(@Valid @RequestBody CompareRequest request) {
        List<Patient> ranked = service.compare(request.patientIds());

        List<Map<String, Object>> rankedEntries = new ArrayList<>();
        int rank = 1;
        for (Patient p : ranked) {
            TriageScore score = p.getTriageScore();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", rank++);
            entry.put("patientId", p.getId());
            entry.put("name", p.getName());
            entry.put("total", score != null ? score.total() : p.getUrgency());
            entry.put("tier", score != null ? score.tier().name() : "UNSCORED");
            entry.put("tierDescription", score != null ? score.tier().description() : "Legacy patient — no clinical inputs");
            entry.put("factors", score != null ? score.factors() : List.of());
            rankedEntries.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ranked", rankedEntries);
        response.put("patientCount", rankedEntries.size());
        return ResponseEntity.ok(response);
    }
}