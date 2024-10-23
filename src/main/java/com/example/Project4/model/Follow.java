package com.example.Project4.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followId;

    @ManyToOne
    @JoinColumn(name = "followerId", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followedOrganizationId", nullable = false)
    private Organization followedOrganization;

    @Column(name = "followed_at")
    private LocalDateTime followedAt;

}