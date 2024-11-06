package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.dto.OrganizationDto;
import com.example.Project4.dto.UserDto;
import com.example.Project4.enums.Status;
import com.example.Project4.model.LoginRequest;
import com.example.Project4.model.LoginResponse;
import com.example.Project4.model.User;
import com.example.Project4.repository.UserRepository;
import com.example.Project4.security.JWTUtils;
import com.example.Project4.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_ReturnsUserDto() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getById(userId, false);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        UserDto result = userService.getById(userId, false);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testLoginUser_SuccessfulLogin() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        Authentication authentication = mock(Authentication.class);
        MyUserDetails userDetails = mock(MyUserDetails.class);
        User user = new User();
        user.setEnabled(true);
        user.setStatus(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("mockJwtToken");

        // Act
        ResponseEntity<?> response = userService.loginUser(loginRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    void testLoginUser_AccountNotVerified() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        Authentication authentication = mock(Authentication.class);
        MyUserDetails userDetails = mock(MyUserDetails.class);
        User user = new User();
        user.setEnabled(false);  // Account not verified

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = userService.loginUser(loginRequest);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
    }


}