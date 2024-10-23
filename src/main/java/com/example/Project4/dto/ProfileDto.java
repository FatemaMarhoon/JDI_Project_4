package com.example.Project4.dto;

import com.example.Project4.model.UserProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePic;
    private UserDto user;

    public ProfileDto(UserProfile profile, boolean details) {
        this.id = profile.getId();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.profilePic = profile.getProfilePic();

        // Only instantiate UserDto if details flag is true
        if (details) {
            this.user = profile.getUser() != null ? new UserDto(profile.getUser(), false) : null;
        }
    }

}
