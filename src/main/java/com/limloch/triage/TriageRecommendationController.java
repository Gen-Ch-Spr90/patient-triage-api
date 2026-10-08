package com.limloch.triage;

import com.limloch.triage.guidance.ClinicalGuidance;
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
public class TriageRecommendationController {

    private final TriageRecommendationService service;

    public TriageRecommendationController(TriageRecommendationService service) {
        this.service = service;
    }

    public record RecommendRequest(@NotEmpty List<String> patientIds) {}

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@Valid @RequestBody RecommendRequest req) {
        TriageRecommendation rec = service.recommend(req.patientIds());

        List<Map<String, Object>> rankedEntries = new ArrayList<>();
        int rank = 1;
        for (Patient p : rec.ranked()) {
            TriageScore score = p.getTriageScore();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", rank++);
            entry.put("patientId", p.getId());
            entry.put("name", p.getName());
            entry.put("total", score != null ? score.total() : p.getUrgency());
            entry.put("tier", score != null ? score.tier().name() : "UNSCORED");
            entry.put("chiefComplaint", p.getChiefComplaint());
            rankedEntries.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ranked", rankedEntries);
        response.put("topPatientId", rankedEntries.isEmpty() ? null : rankedEntries.get(0).get("patientId"));

        ClinicalGuidance guidance = rec.guidance();
        if (guidance != null) {
            Map<String, Object> guidanceMap = new LinkedHashMap<>();
            guidanceMap.put("answer", guidance.answer());
            guidanceMap.put("sources", guidance.sources());
            guidanceMap.put("metrics", guidance.metrics());
            response.put("guidance", guidanceMap);
        }

        return ResponseEntity.ok(response);
    }
}