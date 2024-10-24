package com.example.Project4.controller;

import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.service.VolunteerOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class VolunteerOpportunityController {

    private final VolunteerOpportunityService volunteerOpportunityService;
    @Autowired
    public VolunteerOpportunityController(VolunteerOpportunityService volunteerOpportunityService) {
        this.volunteerOpportunityService = volunteerOpportunityService;
    }

    @GetMapping
    public List<VolunteerOpportunity> getAllOpportunities() {
        return volunteerOpportunityService.getAllOpportunities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerOpportunity> getOpportunityById(@PathVariable Long id) {
        return volunteerOpportunityService.getOpportunityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VolunteerOpportunity createOpportunity(@RequestBody VolunteerOpportunity opportunity) {
        return volunteerOpportunityService.createOpportunity(opportunity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerOpportunity> updateOpportunity(@PathVariable Long id, @RequestBody VolunteerOpportunity opportunityDetails) {
        try {
            VolunteerOpportunity updatedOpportunity = volunteerOpportunityService.updateOpportunity(id, opportunityDetails);
            return ResponseEntity.ok(updatedOpportunity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        try {
            volunteerOpportunityService.deleteOpportunity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<VolunteerOpportunity> searchOpportunities(@RequestParam String title) {
        return volunteerOpportunityService.searchOpportunitiesByTitle(title);
    }
}
