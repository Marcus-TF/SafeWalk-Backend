package com.safewalk.service.impl;

import com.safewalk.dto.AuthRequest;
import com.safewalk.dto.AuthResponse;
import com.safewalk.dto.SignUpRequest;
import com.safewalk.exception.EmailAlreadyExistsException;
import com.safewalk.exception.InvalidCredentialsException;
import com.safewalk.model.User;
import com.safewalk.repository.UserRepository;
import com.safewalk.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignUpRequest signUpRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setName("Marcus Test");
        signUpRequest.setEmail("marcus@test.com");
        signUpRequest.setPassword("securePassword");

        authRequest = new AuthRequest();
        authRequest.setEmail("marcus@test.com");
        authRequest.setPassword("securePassword");

        user = User.builder()
                .id(1L)
                .name("Marcus Test")
                .email("marcus@test.com")
                .password("encodedPassword")
                .notifyHigh(false)
                .notifyMedium(false)
                .notifyLow(false)
                .build();
    }

    @Test
    void signup_WithValidRequest_ShouldReturnAuthResponse() {
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.signup(signUpRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getName(), response.getUser().getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_WithExistingEmail_ShouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.signup(signUpRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
    }

    @Test
    void login_WithNonExistingEmail_ShouldThrowInvalidCredentialsException() {
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WithIncorrectPassword_ShouldThrowInvalidCredentialsException() {
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
    }
}
