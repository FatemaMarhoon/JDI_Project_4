package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.dto.UserDto;
import com.example.Project4.model.LoginRequest;
import com.example.Project4.model.LoginResponse;
import com.example.Project4.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFirstName("testuser");
        GenericDao<UserDto> genericDao = new GenericDao<>();
        genericDao.setObject(userDto);

        when(userService.createUser(any(UserDto.class))).thenReturn(genericDao);
        when(objectMapper.writeValueAsString(any(UserDto.class))).thenReturn("{\"username\":\"testuser\"}");

        ResponseEntity<GenericDao<UserDto>> response = userController.createUser(userDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto, response.getBody().getObject());
    }

    @Test
    void createUser_Failure() throws Exception {
        UserDto userDto = new UserDto();
        GenericDao<UserDto> genericDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        errors.add("User already exists");
        genericDao.setErrors(errors);

        when(userService.createUser(any(UserDto.class))).thenReturn(genericDao);

        ResponseEntity<GenericDao<UserDto>> response = userController.createUser(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errors, response.getBody().getErrors());
    }

//    @Test
//    void loginUser_Success() throws Exception {
//        LoginRequest loginRequest = new LoginRequest();
//        LoginResponse loginResponse = new LoginResponse("token");
//
//        when(userService.loginUser(any(LoginRequest.class))).thenReturn(ResponseEntity.ok(loginResponse));
//
//        ResponseEntity<?> response = userController.loginUser(loginRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(loginResponse, response.getBody());
//    }

    @Test
    void loginUser_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();

        when(userService.loginUser(any(LoginRequest.class))).thenThrow(new RuntimeException("Login failed"));

        ResponseEntity<?> response = userController.loginUser(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void verifyUser_Success() {
        String code = "verificationCode";

        when(userService.verify(code)).thenReturn(true);

        ResponseEntity<?> response = userController.verifyUser(code);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User verified Successfully!", response.getBody());
    }

    @Test
    void verifyUser_Failure() {
        String code = "verificationCode";

        when(userService.verify(code)).thenReturn(false);

        ResponseEntity<?> response = userController.verifyUser(code);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new LoginResponse("Code Verification is wrong. please provide the correct one."), response.getBody());
    }

    @Test
    void requestPasswordReset_Success() {
        String email = "test@example.com";

        when(userService.requestPasswordReset(email)).thenReturn(true);

        ResponseEntity<?> response = userController.requestPasswordReset(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset code sent.", response.getBody());
    }

    @Test
    void requestPasswordReset_UserNotFound() {
        String email = "test@example.com";

        when(userService.requestPasswordReset(email)).thenReturn(false);

        ResponseEntity<?> response = userController.requestPasswordReset(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with this email does not exist.", response.getBody());
    }

    // Add similar tests for resetPassword, changePassword, deactivateUser, and getAllUsers...

}
