package com.example.Project4.repository;

import com.example.Project4.model.Application;
import com.example.Project4.model.User;
import com.example.Project4.model.VolunteerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByUser(User user);
    List<Application> findByOpportunity(VolunteerOpportunity opportunity);
}
