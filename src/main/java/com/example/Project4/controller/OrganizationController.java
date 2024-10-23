package com.example.Project4.controller;

import com.example.Project4.model.Organization;
import com.example.Project4.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // Create a new organization
    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization organization) {
        Organization createdOrganization = organizationService.createOrganization(organization);
        return ResponseEntity.ok(createdOrganization);
    }

    // Get all organizations
    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    // Get an organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable("id") Integer organizationId) {
        return organizationService.getOrganizationById(organizationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update an organization
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable("id") Integer organizationId,
            @RequestBody Organization organizationDetails) {
        Organization updatedOrganization = organizationService.updateOrganization(organizationId, organizationDetails);
        return ResponseEntity.ok(updatedOrganization);
    }

    // Delete an organization
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable("id") Integer organizationId) {
        organizationService.deleteOrganization(organizationId);
        return ResponseEntity.noContent().build();
    }
}
