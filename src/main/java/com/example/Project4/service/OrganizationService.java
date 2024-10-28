package com.example.Project4.service;

import com.example.Project4.model.Organization;
import com.example.Project4.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Project4.enums.Status;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    // Create organization with "Pending" status
    public Organization createOrganization(Organization organization) {
        organization.setStatus(Status.PENDING);
        return organizationRepository.save(organization);
    }

    // Approve organization
    public Organization approveOrganization(Integer organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        organization.setStatus(Status.APPROVED);
        return organizationRepository.save(organization);
    }

    // Reject organization
    public Organization rejectOrganization(Integer organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        organization.setStatus(Status.REJECTED);
        return organizationRepository.save(organization);
    }

    // Only retrieve approved organizations
    public List<Organization> getAllApprovedOrganizations() {
        return organizationRepository.findByStatus(Status.APPROVED);
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
