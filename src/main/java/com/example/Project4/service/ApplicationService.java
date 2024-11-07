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
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ApplicationService {
    private final UserService userService; // Declare UserService
private final VolunteerOpportunityService volunteerOpportunityService;
    private final ApplicationRepository applicationRepository;
    private  final UserRepository userRepository;
    private final VolunteerOpportunityRepository volunteerOpportunityRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, VolunteerOpportunityRepository volunteerOpportunityRepository,UserService userService,VolunteerOpportunityService volunteerOpportunityService) {
        this.applicationRepository = applicationRepository;
        this.userRepository=userRepository;
        this.volunteerOpportunityRepository=volunteerOpportunityRepository;
        this.userService=userService;
        this.volunteerOpportunityService=volunteerOpportunityService;
    }



    // Returns only applications the user has permission to view
    public List<Application> getAllApplications() {
        User currentUser = userService.getCurrentUser();
        if (isVolunteer()) {
            return applicationRepository.findByUser(currentUser);
        } else if (isOrganization()) {
            return applicationRepository.findByOpportunity_Organization(currentUser);
        }
        return new ArrayList<>(); // or throw a permission exception if needed
    }
    public GenericDao<Application> createApplication(Application application) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        // Set the current logged-in user as the applicant
        application.setUser(userService.getCurrentUser());

        // Check if the user is a volunteer
        if (!isVolunteer()) {
            errors.add("Only Volunteers can apply");
            returnDao.setErrors(errors);
            return returnDao;
        }

        // Prevent users from applying on behalf of others
        if (!application.getUser().equals(userService.getCurrentUser())) {
            errors.add("You cannot apply for someone else!");
            returnDao.setErrors(errors);
            return returnDao;
        }

        // Log the incoming application data
        System.out.println("Creating application for user ID: " + application.getUser().getId());
        System.out.println("Creating application with opportunity ID: " + application.getOpportunity().getOpportunityId());

        // Check if opportunity ID is provided
        if (application.getOpportunity() == null || application.getOpportunity().getOpportunityId() == null) {
            errors.add("Opportunity ID must not be null.");
        }

        // Proceed only if no errors
        if (errors.isEmpty()) {
            // Fetch the Opportunity from the database
            VolunteerOpportunity opportunity = volunteerOpportunityRepository.findById(Long.valueOf(application.getOpportunity().getOpportunityId()))
                    .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id " + application.getOpportunity().getOpportunityId()));

            // Set the fetched Opportunity and application date
            application.setOpportunity(opportunity);
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



    // Fetch a single application with access validation
    public GenericDao<Application> getApplicationById(Integer id) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        Optional<Application> applicationOpt = applicationRepository.findById(id);
        if (applicationOpt.isPresent()) {
            Application application = applicationOpt.get();
            if (application.getUser().equals(currentUser) ||
                    (isOrganization() && application.getOpportunity().getOrganization().equals(currentUser))) {
                returnDao.setObject(application);
            } else {
                errors.add("You do not have permission to view this application.");
            }
        } else {
            errors.add("Application not found.");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }
    // Update application only if the volunteer owns it
    public GenericDao<Application> updateApplication(Integer id, Application applicationDetails) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            if (application.getUser().equals(currentUser)) {
                application.setStatus(applicationDetails.getStatus());
                Application updatedApplication = applicationRepository.save(application);
                returnDao.setObject(updatedApplication);
            } else {
                errors.add("You can only update your own applications.");
            }
        } else {
            errors.add("Application not found.");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }

    // Delete only if the volunteer owns the application
    public GenericDao<Void> deleteApplication(Integer id) {
        GenericDao<Void> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            if (application.getUser().equals(currentUser)) {
                applicationRepository.deleteById(id);
            } else {
                errors.add("You can only delete your own applications.");
            }
        } else {
            errors.add("Application not found.");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public GenericDao<List<Application>> getApplicationsByUser(User user) {
        GenericDao<List<Application>> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        // Log current user and the user being passed
        logger.info("Current User: " + currentUser.getEmail());
        logger.info("Requested User: " + user.getEmail());

        // Log the check on whether the currentUser is the same as the passed user or an organization
        if (Objects.equals(currentUser.getId(), user.getId())) {
            logger.info("Current user is the same as the requested user.");
        } else if (isOrganization()) {
            logger.info("Current user is part of an organization and can view other users' applications.");
        } else {
            logger.warn("User is trying to access another user's applications without proper permissions.");
        }

        // If the currentUser is the same as the passed user or the currentUser is allowed to view others' applications
        if (Objects.equals(currentUser.getId(), user.getId())) {
            logger.info("Fetching applications for user: " + user.getEmail());
            List<Application> applications = applicationRepository.findByUser(user);
            returnDao.setObject(applications);
        } else {
            errors.add("You do not have permission to view these applications.");
            logger.error("Permission denied for current user to view applications of user: " + user.getEmail());
            returnDao.setErrors(errors);
        }

        return returnDao;
    }
    public GenericDao<List<Application>> getApplicationsByOpportunity(VolunteerOpportunity opportunity) {
        GenericDao<List<Application>> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        // Fetch the full VolunteerOpportunity object using the provided ID
        Optional<VolunteerOpportunity> vo = volunteerOpportunityService.getOpportunityById(Long.valueOf(opportunity.getOpportunityId()));

        // Log current user and the opportunity being passed
        logger.info("Current User ORg: " + currentUser.getOrganization().getOrganizationId());
        if (vo.isPresent() && vo.get().getOrganization() != null) {
            logger.info("Requested Opportunity ORG: " + vo.get().getOrganization().getOrganizationId());
        } else {
            logger.warn("The opportunity does not have an associated organization.");
        }

        // Check if the current user is part of an organization and if they own the opportunity
        if (isOrganization()) {
            logger.info("Current user is an organization. Checking ownership of the opportunity.");

            // Null check for the opportunity's organization field from the retrieved object
            if (vo.isPresent() && vo.get().getOrganization() != null) {
                if (vo.get().getOrganization().getOrganizationId().equals(currentUser.getOrganization().getOrganizationId())) {
                    logger.info("Current user owns the opportunity.");
                    List<Application> applications = applicationRepository.findByOpportunity(vo.get());
                    returnDao.setObject(applications);
                } else {
                    logger.warn("Current user does not own the opportunity: " + vo.get().getOpportunityId());
                    errors.add("You do not have permission to view applications for this opportunity.");
                    returnDao.setErrors(errors);
                }
            } else {
                logger.warn("The opportunity does not have an associated organization.");
                errors.add("The opportunity does not have an associated organization.");
                returnDao.setErrors(errors);
            }
        } else {
            logger.warn("Current user is not part of an organization, cannot view applications for this opportunity.");
            errors.add("You do not have permission to view applications for this opportunity.");
            returnDao.setErrors(errors);
        }

        return returnDao;
    }


    // Approve application only if the organization owns the opportunity
    public GenericDao<Application> approveApplication(Integer id) {
        return changeApplicationStatus(id, Status.APPROVED, "approve");
    }

    // Reject application only if the organization owns the opportunity
    public GenericDao<Application> rejectApplication(Integer id) {
        return changeApplicationStatus(id, Status.REJECTED, "reject");
    }

    // Log currentUser details and organization association for validation
    private GenericDao<Application> changeApplicationStatus(Integer id, Status status, String action) {
        GenericDao<Application> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        User currentUser = userService.getCurrentUser();

        // Log current user information
        System.out.println("Current User ID: " + currentUser.getId());
        System.out.println("Current User Role: " + userService.getCurrentUserRole().getName());
        System.out.println("Action requested: " + action);

        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();

            // Log the organization associated with the opportunity
            System.out.println("Opportunity Organization ID: " + application.getOpportunity().getOrganization().getOrganizationId());
            System.out.println("Opportunity Organization Name: " + application.getOpportunity().getOrganization().getName());
            System.out.println("current user org:"+currentUser.getOrganization());
            // Check if the current user is an organization and owns the opportunity for this application
            if (isOrganization() && application.getOpportunity().getOrganization().getOrganizationId().equals(currentUser.getOrganization().getOrganizationId())) {
                // Allow status change if the application is pending or if the action is "reject"
                if (application.getStatus() == Status.PENDING || action.equals("reject")) {
                    application.setStatus(status);
                    Application savedApplication = applicationRepository.save(application);
                    returnDao.setObject(savedApplication);
                } else {
                    errors.add("Only pending applications can be approved.");
                }
            } else {
                errors.add("You do not have permission to " + action + " this application.");
            }
        } else {
            errors.add("Application not found.");
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }


    private boolean isVolunteer() {
        return "Volunteer".equals(userService.getCurrentUserRole().getName());
    }

    private boolean isOrganization() {
        return "Organization".equals(userService.getCurrentUserRole().getName());
    }
}
