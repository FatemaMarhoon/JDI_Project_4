package com.example.Project4.dto;

import com.example.Project4.enums.Status;
import com.example.Project4.model.Organization;
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
public class OrganizationDto implements Serializable {
    private Integer id;
    private String name;
    private String contactInfo;
    private String description;
    private Boolean status;
    private UserDto user; // Link to the user who owns this organization

    public OrganizationDto(Organization organization) {
        this.id = organization.getOrganizationId();
        this.name = organization.getName();
        this.contactInfo = organization.getContactInfo();
        this.description = organization.getDescription();
        this.status = organization.getStatus() == Status.PENDING;
        this.user = organization.getUser() != null ? new UserDto(organization.getUser(), false) : null;
    }

    @Override
    public String toString() {
        return "OrganizationDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", user=" + user +
                '}';
    }
}
