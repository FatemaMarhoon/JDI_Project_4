package com.example.Project4.service;

import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.repository.VolunteerOpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository volunteerOpportunityRepository;
    @Autowired
    public VolunteerOpportunityService(VolunteerOpportunityRepository volunteerOpportunityRepository) {
        this.volunteerOpportunityRepository = volunteerOpportunityRepository;
    }

    public List<VolunteerOpportunity> getAllOpportunities() {
        return volunteerOpportunityRepository.findAll();
    }

    public Optional<VolunteerOpportunity> getOpportunityById(Long id) {
        return volunteerOpportunityRepository.findById(id);
    }

    public VolunteerOpportunity createOpportunity(VolunteerOpportunity opportunity) {
        return volunteerOpportunityRepository.save(opportunity);
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
}
