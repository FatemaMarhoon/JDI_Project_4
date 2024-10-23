package com.example.Project4.dto;


import com.example.Project4.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private List<UserDto> users;

    public RoleDto(Role role, boolean details) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();

        // Only instantiate UserDto if details flag is true
        if (details) {
            this.users = role.getUsers() != null ?
                    role.getUsers().stream().map(user -> new UserDto(user, false)).toList() :
                    new ArrayList<>();
        }
    }

}