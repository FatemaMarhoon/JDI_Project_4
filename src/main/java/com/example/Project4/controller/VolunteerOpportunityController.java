package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.service.VolunteerOpportunityService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/opportunities")
public class VolunteerOpportunityController {
    private static final Logger logger = LoggerFactory.getLogger(VolunteerOpportunityController.class);

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

//    @PostMapping
//    public GenericDao<VolunteerOpportunity> createOpportunity(@RequestBody VolunteerOpportunity opportunity) {
//        return volunteerOpportunityService.createOpportunity(opportunity);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOpportunity(@PathVariable Long id, @RequestBody VolunteerOpportunity opportunityDetails) {
        try {
            VolunteerOpportunity updatedOpportunity = volunteerOpportunityService.updateOpportunity(id, opportunityDetails);
            return ResponseEntity.ok(updatedOpportunity);
        } catch (RuntimeException e) {
            // Return a 403 Forbidden status with an error message if it's an organization mismatch
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOpportunity(@PathVariable Long id) {
        try {
            volunteerOpportunityService.deleteOpportunity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Return a 403 Forbidden status with an error message if there's an organization mismatch
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions with a 500 Internal Server Error status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/search")
    public List<VolunteerOpportunity> searchOpportunities(@RequestParam String title) {
        return volunteerOpportunityService.searchOpportunitiesByTitle(title);
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<String> archiveOpportunity(@PathVariable Long id) {
        try {
            VolunteerOpportunity archivedOpportunity = volunteerOpportunityService.archiveOpportunity(id);
            return ResponseEntity.ok("Opportunity archived successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());  // Return the error message from the exception
        }
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<GenericDao<VolunteerOpportunity>> createOpportunityWithFiles(
            @RequestPart("opportunity") VolunteerOpportunity opportunity,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        // Pass files to the service method for handling upload and persistence
        return volunteerOpportunityService.createOpportunityWithFiles(opportunity, List.of(files));
    }

    @GetMapping("/current-org")
    public ResponseEntity<List<VolunteerOpportunity>> getOpportunitiesForCurrentOrganization() {
        try {
            List<VolunteerOpportunity> opportunities = volunteerOpportunityService.getOpportunitiesForCurrentOrganization();
            return ResponseEntity.ok(opportunities);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
