package com.example.Project4.repository;

import com.example.Project4.model.VolunteerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, Long> {
    List<VolunteerOpportunity> findByTitleContainingIgnoreCase(String title);
    List<VolunteerOpportunity> findByEndDateBeforeAndIsArchivedFalse(LocalDate date);

}