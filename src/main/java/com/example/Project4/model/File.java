package com.example.Project4.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "opportunityId", nullable = false)
    private VolunteerOpportunity volunteerOpportunity;

}