package com.example.Project4.security;


import com.example.Project4.model.User;
import com.example.Project4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * MyUserDetailsService is a service class that implements UserDetailsService interface.
 * It is responsible for loading user-specific data during authentication.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userService.findUserByEmailAddress(email);
        return new MyUserDetails(user.orElseThrow(() -> new UsernameNotFoundException("Username/email address not found")));
    }
}