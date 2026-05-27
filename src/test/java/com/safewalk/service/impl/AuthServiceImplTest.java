package com.safewalk.service.impl;

import com.safewalk.dto.AuthRequest;
import com.safewalk.dto.AuthResponse;
import com.safewalk.dto.SignUpRequest;
import com.safewalk.exception.EmailAlreadyExistsException;
import com.safewalk.exception.InvalidCredentialsException;
import com.safewalk.exception.InactiveUserException;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.model.User;
import com.safewalk.model.EmailActivationToken;
import com.safewalk.repository.UserRepository;
import com.safewalk.repository.EmailActivationTokenRepository;
import com.safewalk.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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

    @Mock
    private EmailActivationTokenRepository emailActivationTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignUpRequest signUpRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "backendUrl", "http://localhost:8080");

        signUpRequest = new SignUpRequest();
        signUpRequest.setName("Marcus Test");
        signUpRequest.setEmail("marcus@test.com");
        signUpRequest.setPassword("SecureP@ss123");

        authRequest = new AuthRequest();
        authRequest.setEmail("marcus@test.com");
        authRequest.setPassword("SecureP@ss123");

        user = User.builder()
                .id(1L)
                .name("Marcus Test")
                .email("marcus@test.com")
                .password("encodedPassword")
                .notifyHigh(false)
                .notifyMedium(false)
                .notifyLow(false)
                .isActive(true)
                .build();
    }

    @Test
    void signup_WithValidRequest_ShouldReturnAuthResponse() {
        when(userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.signup(signUpRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getName(), response.getUser().getName());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailActivationTokenRepository, times(1)).save(any(EmailActivationToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void signup_WithExistingEmail_ShouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.signup(signUpRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmailIgnoreCase(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        verify(userRepository, times(1)).findByEmailIgnoreCase(authRequest.getEmail());
    }

    @Test
    void login_WithInactiveUser_ShouldThrowInactiveUserException() {
        user.setIsActive(false);
        when(userRepository.findByEmailIgnoreCase(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(InactiveUserException.class, () -> authService.login(authRequest));
    }

    @Test
    void login_WithDeletedUser_ShouldThrowInvalidCredentialsException() {
        user.setDeletedAt(LocalDateTime.now());
        when(userRepository.findByEmailIgnoreCase(authRequest.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
    }

    @Test
    void login_WithNonExistingEmail_ShouldThrowInvalidCredentialsException() {
        when(userRepository.findByEmailIgnoreCase(authRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WithIncorrectPassword_ShouldThrowInvalidCredentialsException() {
        when(userRepository.findByEmailIgnoreCase(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
    }

    @Test
    void activateAccount_WithValidToken_ShouldActivateUserAndReturnVoid() {
        EmailActivationToken token = new EmailActivationToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        user.setIsActive(false);

        when(emailActivationTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        authService.activateAccount("valid-token");

        assertTrue(user.getIsActive());
        verify(userRepository, times(1)).save(user);
        verify(emailActivationTokenRepository, times(1)).delete(token);
    }

    @Test
    void activateAccount_WithExpiredToken_ShouldThrowIllegalArgumentException() {
        EmailActivationToken token = new EmailActivationToken();
        token.setToken("expired-token");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(emailActivationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () -> authService.activateAccount("expired-token"));
        verify(emailActivationTokenRepository, times(1)).delete(token);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void activateAccount_WithInvalidToken_ShouldThrowResourceNotFoundException() {
        when(emailActivationTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.activateAccount("invalid-token"));
    }
}
