package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Application;
import com.example.Project4.model.User;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericDao<Application>> getApplicationById(@PathVariable Integer id) {
        GenericDao<Application> returnDao = applicationService.getApplicationById(id);
        if (returnDao.getErrors() != null && !returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if any
        }
        return ResponseEntity.ok(returnDao);
    }

    @PostMapping
    public ResponseEntity<GenericDao<Application>> createApplication(@RequestBody Application application) {
        GenericDao<Application> returnDao = applicationService.createApplication(application);
        return ResponseEntity.ok(returnDao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericDao<Application>> updateApplication(@PathVariable Integer id, @RequestBody Application applicationDetails) {
        GenericDao<Application> returnDao = applicationService.updateApplication(id, applicationDetails);
        if (returnDao.getErrors() != null && !returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if any
        }
        return ResponseEntity.ok(returnDao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDao<Void>> deleteApplication(@PathVariable Integer id) {
        GenericDao<Void> returnDao = applicationService.deleteApplication(id);
        if (returnDao.getErrors() != null && !returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if any
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public GenericDao<List<Application>>getApplicationsByUser(@PathVariable Long userId) {
        User user = new User();  // Create a new User instance
        user.setId(userId);     // Set the ID
        return applicationService.getApplicationsByUser(user);
    }

    @GetMapping("/opportunity/{opportunityId}")
    public GenericDao<List<Application>> getApplicationsByOpportunity(@PathVariable Integer opportunityId) {
        VolunteerOpportunity opportunity = new VolunteerOpportunity();  // Create a new Opportunity instance
        opportunity.setOpportunityId(opportunityId);  // Set the ID
        return applicationService.getApplicationsByOpportunity(opportunity);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<GenericDao<Application>> approveApplication(@PathVariable Integer id) {
        GenericDao<Application> returnDao = applicationService.approveApplication(id);
        if (returnDao.getErrors() != null && !returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if any
        }
        return ResponseEntity.ok(returnDao);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<GenericDao<Application>> rejectApplication(@PathVariable Integer id) {
        GenericDao<Application> returnDao = applicationService.rejectApplication(id);
        if (returnDao.getErrors() != null && !returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if any
        }
        return ResponseEntity.ok(returnDao);
    }
}
