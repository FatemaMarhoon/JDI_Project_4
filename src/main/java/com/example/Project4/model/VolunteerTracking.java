package com.example.Project4.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VolunteerTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackingId;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private VolunteerOpportunity opportunity;

    private int hoursContributed;
    private LocalDateTime participationDate;
}
