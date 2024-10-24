package com.example.Project4.service;

import com.example.Project4.dto.RoleDto;
import com.example.Project4.model.Role;
import com.example.Project4.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role createRole(RoleDto roleDto) {
        Role role = new Role(roleDto);
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, RoleDto roleDto) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            Role roleToUpdate = optionalRole.get();
            roleToUpdate.setName(roleDto.getName());
            roleToUpdate.setDescription(roleDto.getDescription());
            return roleRepository.save(roleToUpdate);
        } else {
            throw new RuntimeException("Role not found with id " + id);
        }
    }

    public void deleteRole(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Role not found with id " + id);
        }
    }
}
