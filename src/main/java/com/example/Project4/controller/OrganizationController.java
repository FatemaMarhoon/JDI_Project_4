package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Organization;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // Create a new organization (default to PENDING)
    @PostMapping
    public ResponseEntity<GenericDao<Organization>> createOrganization(@RequestBody Organization organization) {
        GenericDao<Organization> returnDao = organizationService.createOrganization(organization);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.ok(returnDao); // Return created organization
    }

    // Get all approved organizations
    @GetMapping("/approved") // For getting only approved organizations
    public ResponseEntity<List<Organization>> getAllApprovedOrganizations() {
        List<Organization> organizations = organizationService.getAllApprovedOrganizations();
        return ResponseEntity.ok(organizations);
    }

    // Approve organization (Admin only)
    @PutMapping("/{id}/approve")
    public ResponseEntity<GenericDao<Organization>> approveOrganization(@PathVariable("id") Integer organizationId) {
        GenericDao<Organization> returnDao = organizationService.approveOrganization(organizationId);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.ok(returnDao); // Return approved organization
    }

    // Reject organization (Admin only)
    @PutMapping("/{id}/reject")
    public ResponseEntity<GenericDao<Organization>> rejectOrganization(@PathVariable("id") Integer organizationId) {
        GenericDao<Organization> returnDao = organizationService.rejectOrganization(organizationId);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.ok(returnDao); // Return rejected organization
    }

    @GetMapping
    public ResponseEntity<GenericDao<List<Organization>>> getAllOrganizations() {
        GenericDao<List<Organization>> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Assuming organizationService.getAllOrganizations() returns a GenericDao containing a list of organizations
        GenericDao<List<Organization>> daoResponse = organizationService.getAllOrganizations();

        if (daoResponse != null && daoResponse.getObject() != null && !daoResponse.getObject().isEmpty()) {
            // If organizations are found, return them
            returnDao.setObject(daoResponse.getObject());
            return ResponseEntity.ok(returnDao);
        } else {
            // If no organizations are found or there's an error
            errors.add("No organizations found or access denied.");
            returnDao.setErrors(errors);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(returnDao);
        }
    }


    // Get an organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<GenericDao<Organization>> getOrganizationById(@PathVariable("id") Integer organizationId) {
        GenericDao<Organization> returnDao = organizationService.getOrganizationByIdDao(organizationId);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.ok(returnDao); // Return organization found
    }

    // Update an organization
    @PutMapping("/{id}")
    public ResponseEntity<GenericDao<Organization>> updateOrganization(
            @PathVariable("id") Integer organizationId,
            @RequestBody Organization organizationDetails) {
        GenericDao<Organization> returnDao = organizationService.updateOrganization(organizationId, organizationDetails);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.ok(returnDao); // Return updated organization
    }

    // Delete an organization
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDao<Void>> deleteOrganization(@PathVariable("id") Integer organizationId) {
        GenericDao<Void> returnDao = organizationService.deleteOrganization(organizationId);
        if (!returnDao.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(returnDao); // Return errors if present
        }
        return ResponseEntity.noContent().build(); // No content to return on successful deletion
    }

    @GetMapping("/search")
    public List<Organization> searchOrg(@RequestParam String name) {
        return organizationService.searchOrgByName(name);
    }
}
