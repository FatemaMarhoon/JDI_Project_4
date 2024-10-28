package com.example.Project4.repository;
import com.example.Project4.model.VolunteerTracking;
import com.example.Project4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerTrackingRepository extends JpaRepository<VolunteerTracking, Long> {
    List<VolunteerTracking> findByVolunteer(User volunteer);
}