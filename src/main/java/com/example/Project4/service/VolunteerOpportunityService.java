package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.File;
import com.example.Project4.model.Role;
import com.example.Project4.model.VolunteerOpportunity;
import com.example.Project4.repository.VolunteerOpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository volunteerOpportunityRepository;
    private final UserService userService; // Declare UserService

    private final S3Service s3Service;
    @Autowired
    public VolunteerOpportunityService(VolunteerOpportunityRepository volunteerOpportunityRepository, UserService userService,S3Service s3Service) {
        this.volunteerOpportunityRepository = volunteerOpportunityRepository;
        this.userService = userService;
        this.s3Service=s3Service;
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
            errors.add("Only Orgs can add");
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

    public CompletableFuture<GenericDao<VolunteerOpportunity>> createOpportunityWithFiles(
            VolunteerOpportunity opportunity, List<MultipartFile> files) throws IOException {

        GenericDao<VolunteerOpportunity> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (!isOrganization()) {
            errors.add("Only Organizations can create opportunities");
            returnDao.setErrors(errors);
            return CompletableFuture.completedFuture(returnDao);
        }

        List<CompletableFuture<File>> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFiles.add(uploadFileToS3(file, opportunity));  // Pass opportunity to method
        }

        return CompletableFuture.allOf(uploadedFiles.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<File> savedFiles = uploadedFiles.stream().map(CompletableFuture::join).toList();
                    opportunity.setFiles(savedFiles);
//                    System.out.println("currrent user:"+userService.getCurrentUser());
                    System.out.println("current org"+userService.getCurrentUserOrg());
//                    opportunity.setOrganization(userService.getCurrentUserOrg());
                    VolunteerOpportunity savedOpportunity = volunteerOpportunityRepository.save(opportunity);
                    returnDao.setObject(savedOpportunity);
                    return returnDao;
                }).exceptionally(e -> {
                    errors.add("Error saving opportunity: " + e.getMessage());
                    returnDao.setErrors(errors);
                    return returnDao;
                });
    }

    private CompletableFuture<File> uploadFileToS3(MultipartFile file, VolunteerOpportunity opportunity) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return s3Service.uploadFile(file.getBytes(), fileName, contentType)
                .thenApply(url -> {
                    File savedFile = new File(fileName, url);
                    savedFile.setVolunteerOpportunity(opportunity);  // Set the opportunity here
                    return savedFile;
                });
    }




}
