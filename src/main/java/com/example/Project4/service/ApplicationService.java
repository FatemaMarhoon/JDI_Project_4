package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.enums.Status;
import com.example.Project4.model.*;
import com.example.Project4.repository.ApplicationRepository;
import com.example.Project4.repository.UserRepository;
import com.example.Project4.repository.VolunteerOpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final UserService userService; // Declare UserService

    private final ApplicationRepository applicationRepository;
    private  final UserRepository userRepository;
    private final VolunteerOpportunityRepository volunteerOpportunityRepository;
    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, VolunteerOpportunityRepository volunteerOpportunityRepository,UserService userService) {
        this.applicationRepository = applicationRepository;
        this.userRepository=userRepository;
        this.volunteerOpportunityRepository=volunteerOpportunityRepository;
        this.userService=userService;
    }



    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public GenericDao<Application> createApplication(Application application) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Check if the user is an admin
        if (!isVolunteer()) {
            errors.add("Only Volunteers can Apply");
            returnDao.setErrors(errors); // Return immediately with the error
            return returnDao;
        }


        // Log the incoming application object
        System.out.println("Creating application with user ID: " + application.getUser().getId());
        System.out.println("Creating application with opportunity ID: " + application.getOpportunity().getOpportunityId());

        // Check if userId or opportunityId is null
        if (application.getUser() == null || application.getUser().getId() == null) {
            errors.add("User ID must not be null.");
        }
        if (application.getOpportunity() == null || application.getOpportunity().getOpportunityId() == null) {
            errors.add("Opportunity ID must not be null.");
        }

        // Proceed only if no errors
        if (errors.isEmpty()) {
            // Fetch the User and VolunteerOpportunity from the database
            User user = userRepository.findById(application.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + application.getUser().getId()));

            VolunteerOpportunity opportunity = volunteerOpportunityRepository.findById(Long.valueOf(application.getOpportunity().getOpportunityId()))
                    .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id " + application.getOpportunity().getOpportunityId()));

            // Set the fetched User and Opportunity in the Application
            application.setUser(user);
            application.setOpportunity(opportunity);

            // Set the applied date
            application.setAppliedAt(LocalDateTime.now());

            try {
                // Save the Application
                Application savedApplication = applicationRepository.save(application);
                returnDao.setObject(savedApplication);
            } catch (Exception e) {
                errors.add("Error saving application: " + e.getMessage());
                returnDao.setErrors(errors);
            }
        } else {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }


    public GenericDao<Application> getApplicationById(Integer id) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        Optional<Application> application = applicationRepository.findById(id);
        if (application.isPresent()) {
            returnDao.setObject(application.get());
        } else {
            errors.add("Application not found");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Application> updateApplication(Integer id, Application applicationDetails) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Check if the user is an admin
        if (!isVolunteer()) {
            errors.add("Only Volunteers can Apply");
            returnDao.setErrors(errors); // Return immediately with the error
            return returnDao;
        }


        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            errors.add("Application not found");
        } else {
            Application application = optionalApplication.get();
            application.setStatus(applicationDetails.getStatus());
            Application updatedApplication = applicationRepository.save(application);
            returnDao.setObject(updatedApplication);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Void> deleteApplication(Integer id) {
        GenericDao<Void> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (applicationRepository.findById(id).isEmpty()) {
            errors.add("Application not found");
        } else {
            applicationRepository.deleteById(id);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public List<Application> getApplicationsByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public List<Application> getApplicationsByOpportunity(VolunteerOpportunity opportunity) {
        return applicationRepository.findByOpportunity(opportunity);
    }

    public GenericDao<Application> approveApplication(Integer id) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            errors.add("Application not found");
        } else {
            Application application = optionalApplication.get();
            if (!application.getStatus().equals("Pending")) {
                errors.add("Only pending applications can be approved");
            } else {
                application.setStatus(Status.APPROVED);
                Application savedApplication = applicationRepository.save(application);
                returnDao.setObject(savedApplication);
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Application> rejectApplication(Integer id) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            errors.add("Application not found");
        } else {
            Application application = optionalApplication.get();
            application.setStatus(Status.REJECTED);
            Application savedApplication = applicationRepository.save(application);
            returnDao.setObject(savedApplication);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }
    private boolean isVolunteer() {
        Role currentRole = userService.getCurrentUserRole();
        return currentRole != null && currentRole.getName().equals("Volunteer"); // Adjust based on your Role enum
    }
}
