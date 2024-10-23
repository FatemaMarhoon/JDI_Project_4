package com.example.Project4.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "opportunityId", nullable = false)
    private VolunteerOpportunity opportunity;

    private String status; // e.g., pending, accepted, rejected

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

}