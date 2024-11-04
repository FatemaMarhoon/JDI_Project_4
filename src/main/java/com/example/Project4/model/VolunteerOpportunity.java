package com.example.Project4.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class VolunteerOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer opportunityId;

    private String title;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "volunteerOpportunity", cascade = CascadeType.ALL)
    private List<File> files; // List of uploaded files

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "organizationId", nullable = false)
    private Organization organization;

    @Column(name = "is_archived")
    private boolean isArchived = false; // Default to not archived

}
