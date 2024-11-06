package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.dto.UserDto;
import com.example.Project4.model.LoginRequest;
import com.example.Project4.model.Role;
import com.example.Project4.model.User;
import com.example.Project4.security.MyUserDetails;
import com.example.Project4.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @Mock
    private MyUserDetails mockedUserDetails;  // Mock MyUserDetails

    @Mock
    private User mockedUser; // Mock User

    @Mock
    Role mockedRole;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext and Authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock getPrincipal to return mocked MyUserDetails
        when(authentication.getPrincipal()).thenReturn(mockedUserDetails);

        // Mock the getCurrentLoggedInUser() method to return the mocked User object
        when(userService.getCurrentLoggedInUser()).thenReturn(mockedUser);

        // Mock the user's role
        when(mockedUser.getRole()).thenReturn(mockedRole);
        when(mockedRole.getName()).thenReturn("User"); // or "Admin" for testing other cases
    }


    @Test
    public void testCreateUser_BadRequest() throws Exception {
        // Simulate exception during user creation
        when(userService.createUser(any(UserDto.class))).thenThrow(new RuntimeException());

        // Call the endpoint
        ResponseEntity<GenericDao<UserDto>> response = userController.createUser(new UserDto());

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRequestPasswordReset_UserNotFound() {
        String email = "test@example.com";

        when(userService.requestPasswordReset(email)).thenReturn(false);

        // Call the endpoint
        ResponseEntity<?> response = userController.requestPasswordReset(email);

        // Verify the results
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with this email does not exist.", response.getBody());
    }

    @Test
    public void testGetAllUsers_Unauthorized() {
        // Simulate non-admin access
        when(userService.getCurrentLoggedInUser().getRole().getName()).thenReturn("User");

        // Call the endpoint
        ResponseEntity<GenericDao<List<UserDto>>> response = userController.getAllUsers();

        // Verify the results
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllUsers_Success() {
        // Mock admin role and user list
        UserDto userDto = new UserDto();
        GenericDao<List<UserDto>> genericDao = new GenericDao<>();
        genericDao.setObject(Collections.singletonList(userDto));

        when(userService.getCurrentLoggedInUser().getRole().getName()).thenReturn("Admin");
        when(userService.getAll(false)).thenReturn(Collections.singletonList(userDto));

        // Call the endpoint
        ResponseEntity<GenericDao<List<UserDto>>> response = userController.getAllUsers();

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(genericDao.getObject(), response.getBody().getObject());
    }
}
