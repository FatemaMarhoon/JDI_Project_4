package com.example.Project4.controller;

import com.example.Project4.model.VolunteerTracking;
import com.example.Project4.model.User;
import com.example.Project4.repository.VolunteerTrackingRepository;
import com.example.Project4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public class VolunteerTrackingService {

    private final VolunteerTrackingRepository trackingRepository;
    private final UserRepository userRepository;

    @Autowired
    public VolunteerTrackingService(VolunteerTrackingRepository trackingRepository, UserRepository userRepository) {
        this.trackingRepository = trackingRepository;
        this.userRepository = userRepository;
    }

    // Retrieve volunteer's participation history
    public List<VolunteerTracking> getVolunteerHistory(Long userId) {
        User volunteer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        return trackingRepository.findByVolunteer(volunteer);
    }

    // Record volunteer participation
    public VolunteerTracking recordParticipation(VolunteerTracking tracking) {
        return trackingRepository.save(tracking);
    }



}
