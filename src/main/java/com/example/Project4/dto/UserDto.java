package com.example.Project4.dto;

import com.example.Project4.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean status;
    @JsonIgnore
    private String verificationCode;
    @JsonIgnore
    private boolean enabled;
    @JsonIgnore
    private String resetToken;
    @JsonIgnore
    private LocalDateTime tokenExpirationTime;
    private RoleDto role;
    private ProfileDto profile;
    private OrganizationDto organizationDto;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isOrganization;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    private boolean isVolunteer;

    public UserDto(User user, boolean details) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.firstName= user.getFirstName();
        this.lastName= user.getLastName();
        this.role = user.getRole() != null ? new RoleDto(user.getRole(), false) : null;

        // Only instantiate ProfileDto if details flag is true
        if (details) {
            this.profile = user.getProfile() != null ? new ProfileDto(user.getProfile(), false) : null;
        }

        this.verificationCode = user.getVerificationCode();
        this.enabled = user.isEnabled();
        this.resetToken = user.getResetToken();
        this.tokenExpirationTime = user.getTokenExpirationTime();


    }

    @Override
    public String toString() {
        return "UserDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", profile=" + profile +
                // Add other fields here
                '}';
    }


}