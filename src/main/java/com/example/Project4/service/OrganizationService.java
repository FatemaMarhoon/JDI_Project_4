package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Organization;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Project4.enums.Status;
import com.example.Project4.service.UserService; // Import UserService
import com.example.Project4.model.Role; // Import Role

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserService userService; // Declare UserService

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, UserService userService) {
        this.organizationRepository = organizationRepository;
        this.userService = userService; // Initialize UserService
    }

    // Create organization with "Pending" status
    public GenericDao<Organization> createOrganization(Organization organization) {
        GenericDao<Organization> returnDao = new GenericDao<>();
        organization.setStatus(Status.PENDING);
        Organization savedOrganization = organizationRepository.save(organization);
        returnDao.setObject(savedOrganization);
        return returnDao;
    }

    // Approve organization
    public GenericDao<Organization> approveOrganization(Integer organizationId) {
        GenericDao<Organization> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Check if the user is an admin
        if (!isAdmin()) {
            errors.add("Only admins can approve organizations");
            returnDao.setErrors(errors); // Return immediately with the error
            return returnDao;
        }

        // Find the organization by ID
        Optional<Organization> optionalOrganization = organizationRepository.findById(organizationId);
        if (optionalOrganization.isEmpty()) {
            errors.add("Organization not found");
            returnDao.setErrors(errors);
            return returnDao; // Return immediately if organization is not found
        } else {
            Organization organization = optionalOrganization.get();

            // Check if the current status allows approval
            if (organization.getStatus() != Status.PENDING) {
                errors.add("Only pending organizations can be approved");
            }

            // If there are no errors, approve the organization
            if (errors.isEmpty()) {
                organization.setStatus(Status.APPROVED);
                Organization savedOrganization = organizationRepository.save(organization);
                returnDao.setObject(savedOrganization);
            }
        }

        // Set errors in returnDao if any exist
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }



    // Reject organization
    public GenericDao<Organization> rejectOrganization(Integer organizationId) {
        GenericDao<Organization> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Check if the user is an admin
        if (!isAdmin()) {
            errors.add("Only admins can reject organizations");
        }

        Optional<Organization> optionalOrganization = organizationRepository.findById(organizationId);
        if (optionalOrganization.isEmpty()) {
            errors.add("Organization not found");
        } else {
            Organization organization = optionalOrganization.get();
            organization.setStatus(Status.REJECTED);
            Organization savedOrganization = organizationRepository.save(organization);
            returnDao.setObject(savedOrganization);
        }

        // If there are any errors, set them in returnDao
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
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
    public GenericDao<Organization> getOrganizationById(Integer organizationId) {
        GenericDao<Organization> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isPresent()) {
            returnDao.setObject(organization.get());
        } else {
            errors.add("Organization not found");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    // Update an organization
    public GenericDao<Organization> updateOrganization(Integer organizationId, Organization organizationDetails) {
        GenericDao<Organization> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        Optional<Organization> optionalOrganization = organizationRepository.findById(organizationId);
        if (optionalOrganization.isEmpty()) {
            errors.add("Organization not found");
        } else {
            Organization organization = optionalOrganization.get();
            organization.setName(organizationDetails.getName());
            organization.setContactInfo(organizationDetails.getContactInfo());
            organization.setDescription(organizationDetails.getDescription());

            Organization updatedOrganization = organizationRepository.save(organization);
            returnDao.setObject(updatedOrganization);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    // Delete an organization
    public GenericDao<Void> deleteOrganization(Integer organizationId) {
        GenericDao<Void> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (!isAdmin()) {
            errors.add("Only admins can delete organizations");
        }

        if (organizationRepository.findById(organizationId).isEmpty()) {
            errors.add("Organization not found");
        } else {
            organizationRepository.deleteById(organizationId);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    // Helper method to check if the current user is an admin
    private boolean isAdmin() {
        Role currentRole = userService.getCurrentUserRole();
        return currentRole != null && currentRole.getName().equals("Admin"); // Adjust based on your Role enum
    }

    public List<Organization> searchOrgByName(String name) {
        List<Organization> allMatchingOrganizations = organizationRepository.findByNameContainingIgnoreCase(name);
        // Filter to keep only approved organizations
        return allMatchingOrganizations.stream()
                .filter(org -> org.getStatus() == Status.APPROVED)
                .toList();
    }

}
