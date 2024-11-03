package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Application;
import com.example.Project4.model.Role;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.repository.VolunteerOpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository volunteerOpportunityRepository;
    private final UserService userService; // Declare UserService

    @Autowired
    public VolunteerOpportunityService(VolunteerOpportunityRepository volunteerOpportunityRepository, UserService userService) {
        this.volunteerOpportunityRepository = volunteerOpportunityRepository;
        this.userService = userService;
    }

    public List<VolunteerOpportunity> getAllOpportunities() {
        return volunteerOpportunityRepository.findAll();
    }

    public Optional<VolunteerOpportunity> getOpportunityById(Long id) {
        return volunteerOpportunityRepository.findById(id);
    }

    public GenericDao<VolunteerOpportunity> createOpportunity(VolunteerOpportunity opportunity) {
        GenericDao<VolunteerOpportunity> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Check if the user is an admin
        if (!isOrganization()) {
            errors.add("Only Volunteers can Apply");
            returnDao.setErrors(errors); // Return immediately with the error
            return returnDao;
        }
        try {

            VolunteerOpportunity savedVolunteerOpportunity = volunteerOpportunityRepository.save(opportunity);
            returnDao.setObject(savedVolunteerOpportunity);
        } catch (Exception e) {
            errors.add("Error saving application: " + e.getMessage());
            returnDao.setErrors(errors);
        }
        return   returnDao;
    }

    public VolunteerOpportunity updateOpportunity(Long id, VolunteerOpportunity opportunityDetails) {
        return volunteerOpportunityRepository.findById(id).map(opportunity -> {
            opportunity.setTitle(opportunityDetails.getTitle());
            opportunity.setDescription(opportunityDetails.getDescription());
            opportunity.setLocation(opportunityDetails.getLocation());
            opportunity.setStartDate(opportunityDetails.getStartDate());
            opportunity.setEndDate(opportunityDetails.getEndDate());
            return volunteerOpportunityRepository.save(opportunity);
        }).orElseThrow(() -> new RuntimeException("Opportunity not found with id " + id));
    }

    public void deleteOpportunity(Long id) {
        VolunteerOpportunity opportunity = volunteerOpportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id " + id));
        volunteerOpportunityRepository.delete(opportunity);
    }

    public List<VolunteerOpportunity> searchOpportunitiesByTitle(String title) {
        return volunteerOpportunityRepository.findByTitleContainingIgnoreCase(title);
    }

    private boolean isOrganization() {
        Role currentRole = userService.getCurrentUserRole();
        return currentRole != null && currentRole.getName().equals("Organization"); // Adjust based on your Role enum
    }

    public VolunteerOpportunity archiveOpportunity(Long id) {
        return volunteerOpportunityRepository.findById(id).map(opportunity -> {
            opportunity.setArchived(true);
            return volunteerOpportunityRepository.save(opportunity);
        }).orElseThrow(() -> new RuntimeException("Opportunity not found with id " + id));
    }

    public void archiveExpiredOpportunities() {
        List<VolunteerOpportunity> expiredOpportunities = volunteerOpportunityRepository
                .findByEndDateBeforeAndIsArchivedFalse(LocalDate.now());

        for (VolunteerOpportunity opportunity : expiredOpportunities) {
            opportunity.setArchived(true);
        }

        volunteerOpportunityRepository.saveAll(expiredOpportunities);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void archiveExpiredOpportunitiesDaily() {
        archiveExpiredOpportunities();
    }





}
