package com.example.Project4.service;

import com.example.Project4.model.Organization;
import com.example.Project4.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    // Create a new organization
    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    // Get all organizations
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    // Get an organization by ID
    public Optional<Organization> getOrganizationById(Integer organizationId) {
        return organizationRepository.findById(organizationId);
    }

    // Update an organization
    public Organization updateOrganization(Integer organizationId, Organization organizationDetails) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setName(organizationDetails.getName());
        organization.setContactInfo(organizationDetails.getContactInfo());
        organization.setDescription(organizationDetails.getDescription());

        return organizationRepository.save(organization);
    }

    // Delete an organization
    public void deleteOrganization(Integer organizationId) {
        organizationRepository.deleteById(organizationId);
    }
}
