package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.File;
import com.example.Project4.model.Organization;
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
    private final UserService userService;
    private final OrganizationService organizationService;
    private final S3Service s3Service;

    @Autowired
    public VolunteerOpportunityService(
            VolunteerOpportunityRepository volunteerOpportunityRepository,
            UserService userService,
            S3Service s3Service,
            OrganizationService organizationService) {
        this.volunteerOpportunityRepository = volunteerOpportunityRepository;
        this.userService = userService;
        this.s3Service = s3Service;
        this.organizationService = organizationService;
    }

    // Basic CRUD Operations
    public List<VolunteerOpportunity> getAllOpportunities() {
        return volunteerOpportunityRepository.findAll();
    }

    public Optional<VolunteerOpportunity> getOpportunityById(Long id) {
        return volunteerOpportunityRepository.findById(id);
    }

    public GenericDao<VolunteerOpportunity> createOpportunity(VolunteerOpportunity opportunity) {
        GenericDao<VolunteerOpportunity> result = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (!isCurrentUserOrganization()) {
            errors.add("Only organizations can add opportunities.");
            result.setErrors(errors);
            return result;
        }

        try {
            opportunity.setOrganization(userService.getCurrentUserOrg());
            VolunteerOpportunity savedOpportunity = volunteerOpportunityRepository.save(opportunity);
            result.setObject(savedOpportunity);
        } catch (Exception e) {
            errors.add("Error saving opportunity: " + e.getMessage());
            result.setErrors(errors);
        }
        return result;
    }

    public VolunteerOpportunity updateOpportunity(Long id, VolunteerOpportunity opportunityDetails) {
        VolunteerOpportunity existingOpportunity = volunteerOpportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id: " + id));

        Organization currentUserOrg = userService.getCurrentUserOrg();
        Organization opportunityOrg = Optional.ofNullable(opportunityDetails.getOrganization())
                .orElse(existingOpportunity.getOrganization());

        if (!currentUserOrg.getOrganizationId().equals(opportunityOrg.getOrganizationId())) {
            throw new RuntimeException("Cannot update opportunities of another organization");
        }

        existingOpportunity.setTitle(opportunityDetails.getTitle());
        existingOpportunity.setDescription(opportunityDetails.getDescription());
        existingOpportunity.setLocation(opportunityDetails.getLocation());
        existingOpportunity.setStartDate(opportunityDetails.getStartDate());
        existingOpportunity.setEndDate(opportunityDetails.getEndDate());
        return volunteerOpportunityRepository.save(existingOpportunity);
    }

    public void deleteOpportunity(Long id) {
        VolunteerOpportunity existingOpportunity = volunteerOpportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id: " + id));

        Organization currentUserOrg = userService.getCurrentUserOrg();
        Organization opportunityOrg = existingOpportunity.getOrganization();

        if (!currentUserOrg.getOrganizationId().equals(opportunityOrg.getOrganizationId())) {
            throw new RuntimeException("Cannot delete opportunities of another organization");
        }

        volunteerOpportunityRepository.delete(existingOpportunity);
    }

    public List<VolunteerOpportunity> searchOpportunitiesByTitle(String title) {
        return volunteerOpportunityRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<VolunteerOpportunity> getOpportunitiesForCurrentOrganization() {
        Organization currentOrg = userService.getCurrentUserOrg();
        if (currentOrg == null) {
            throw new RuntimeException("No organization found for the current user.");
        }
        return volunteerOpportunityRepository.findByOrganization(currentOrg);
    }

    // Helper Methods
    private boolean isCurrentUserOrganization() {
        Role currentRole = userService.getCurrentUserRole();
        return currentRole != null && "Organization".equals(currentRole.getName());
    }

    // Archiving Methods
    public VolunteerOpportunity archiveOpportunity(Long id) {
        VolunteerOpportunity existingOpportunity = volunteerOpportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id: " + id));

        Organization currentUserOrg = userService.getCurrentUserOrg();
        Organization opportunityOrg = existingOpportunity.getOrganization();

        if (!currentUserOrg.getOrganizationId().equals(opportunityOrg.getOrganizationId())) {
            throw new RuntimeException("Cannot archive opportunities of another organization");
        }

        existingOpportunity.setArchived(true);
        return volunteerOpportunityRepository.save(existingOpportunity);
    }


    public void archiveExpiredOpportunities() {
        List<VolunteerOpportunity> expiredOpportunities = volunteerOpportunityRepository
                .findByEndDateBeforeAndIsArchivedFalse(LocalDate.now());

        expiredOpportunities.forEach(opportunity -> opportunity.setArchived(true));
        volunteerOpportunityRepository.saveAll(expiredOpportunities);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void archiveExpiredOpportunitiesDaily() {
        archiveExpiredOpportunities();
    }

    // File Upload Handling
    public CompletableFuture<GenericDao<VolunteerOpportunity>> createOpportunityWithFiles(
            VolunteerOpportunity opportunity, List<MultipartFile> files) throws IOException {

        GenericDao<VolunteerOpportunity> result = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (!isCurrentUserOrganization()) {
            errors.add("Only organizations can create opportunities.");
            result.setErrors(errors);
            return CompletableFuture.completedFuture(result);
        }

        opportunity.setOrganization(userService.getCurrentUserOrg());
        List<CompletableFuture<File>> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            uploadedFiles.add(uploadFileToS3(file, opportunity));
        }

        return CompletableFuture.allOf(uploadedFiles.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<File> savedFiles = uploadedFiles.stream().map(CompletableFuture::join).toList();
                    opportunity.setFiles(savedFiles);
                    result.setObject(volunteerOpportunityRepository.save(opportunity));
                    return result;
                }).exceptionally(e -> {
                    errors.add("Error saving opportunity with files: " + e.getMessage());
                    result.setErrors(errors);
                    return result;
                });
    }

    private CompletableFuture<File> uploadFileToS3(MultipartFile file, VolunteerOpportunity opportunity) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return s3Service.uploadFile(file.getBytes(), fileName, contentType)
                .thenApply(url -> {
                    File savedFile = new File(fileName, url);
                    savedFile.setVolunteerOpportunity(opportunity);
                    return savedFile;
                });
    }
}
