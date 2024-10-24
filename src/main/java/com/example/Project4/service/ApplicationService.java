package com.example.Project4.service;

import com.example.Project4.model.Application;
import com.example.Project4.model.User;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<Application> getApplicationById(Integer id) {
        return applicationRepository.findById(id);
    }

    public Application createApplication(Application application) {
        application.setAppliedAt(LocalDateTime.now());
        return applicationRepository.save(application);
    }

    public Application updateApplication(Integer id, Application applicationDetails) {
        return applicationRepository.findById(id).map(application -> {
            application.setStatus(applicationDetails.getStatus());
            return applicationRepository.save(application);
        }).orElseThrow(() -> new RuntimeException("Application not found with id " + id));
    }

    public void deleteApplication(Integer id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id " + id));
        applicationRepository.delete(application);
    }

    public List<Application> getApplicationsByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public List<Application> getApplicationsByOpportunity(VolunteerOpportunity opportunity) {
        return applicationRepository.findByOpportunity(opportunity);
    }
}
