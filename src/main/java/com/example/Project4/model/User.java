package com.example.Project4.model;


import com.example.Project4.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    @JsonIgnore

    private String password;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "verification_code", length = 64)
    @JsonIgnore

    private String verificationCode;

    @Column(name = "isEnabled")
    private boolean enabled;

    @Column(name = "resetToken")
    @JsonIgnore

    private String resetToken;

    @Column(name = "tokenExpirationTime")
    private LocalDateTime tokenExpirationTime;

    @ManyToOne
    @JoinColumn(name = "roleId")
    @ToString.Exclude
    private Role role;

    @OneToOne
    @JoinColumn(name = "profileId")
    @ToString.Exclude
    private UserProfile profile;





    public User(UserDto dto) {
        this.id = dto.getId();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.status = dto.getStatus();
        this.firstName=dto.getFirstName();

        this.lastName= dto.getLastName();
        this.role = dto.getRole() != null ?
                new Role(dto.getRole()) :
                null;
        this.profile = dto.getProfile() != null ?
                new UserProfile(dto.getProfile()) :
                null;

        this.verificationCode = dto.getVerificationCode();
        this.enabled = dto.isEnabled();
        this.resetToken = dto.getResetToken();
        this.tokenExpirationTime = dto.getTokenExpirationTime();
    }
}
