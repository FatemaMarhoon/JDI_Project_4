package com.example.Project4.model;


import com.example.Project4.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Organization {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer organizationId;

    private String name;
    private String contactInfo;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;


    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "userId")

    private User user;
}