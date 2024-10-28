package com.example.Project4.repository;

import com.example.Project4.enums.Status;
import com.example.Project4.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    List<Organization> findByStatus(Status status);  // Use Status directly, not Organization.Status

}
