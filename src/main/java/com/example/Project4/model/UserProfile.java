package com.example.Project4.model;


import com.example.Project4.dto.ProfileDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"profileId\"", nullable = false)
    private Long id;

    @Column(name = "\"firstName\"", nullable = false)
    private String firstName;

    @Column(name = "\"lastName\"", nullable = false)
    private String lastName;

    @Column(name = "\"profilePic\"")
    private String profilePic;

    @OneToOne(mappedBy = "profile", orphanRemoval = true)
    @JsonIgnore
    private User user;

    public UserProfile(ProfileDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.profilePic = dto.getProfilePic();
        this.user = dto.getUser() != null ?
                new User(dto.getUser()) :
                null;
    }
}
