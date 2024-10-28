package com.example.Project4.controller;

import com.example.Project4.model.VolunteerTracking;
import com.example.Project4.service.VolunteerTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerTrackingController {

    private final VolunteerTrackingService trackingService;

    @Autowired
    public VolunteerTrackingController(VolunteerTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    // Endpoint to get volunteer history
    @GetMapping("/{userId}/history")
    public List<VolunteerTracking> getVolunteerHistory(@PathVariable Long userId) {
        return trackingService.getVolunteerHistory(userId);
    }

    // Endpoint to record participation
    @PostMapping("/record")
    public VolunteerTracking recordParticipation(@RequestBody VolunteerTracking tracking) {
        return trackingService.recordParticipation(tracking);
    }
}
