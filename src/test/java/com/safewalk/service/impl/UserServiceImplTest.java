package com.safewalk.service.impl;

import com.safewalk.dto.UserResponse;
import com.safewalk.dto.UserUpdateRequest;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.exception.WeakPasswordException;
import com.safewalk.model.PasswordResetToken;
import com.safewalk.model.User;
import com.safewalk.repository.PasswordResetTokenRepository;
import com.safewalk.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "frontUrl", "http://test-front.url");

        user = User.builder()
                .id(1L)
                .name("Marcus User")
                .email("marcus@user.com")
                .password("encodedPassword")
                .notifyHigh(true)
                .notifyMedium(false)
                .notifyLow(true)
                .build();

        updateRequest = new UserUpdateRequest();
        updateRequest.setName("Marcus Updated");
        updateRequest.setEmail("marcus.new@user.com");
        updateRequest.setPassword("NewSecureP@ss123");
        updateRequest.setNotifyHigh(false);
        updateRequest.setNotifyMedium(true);
        updateRequest.setNotifyLow(false);
    }

    @Test
    void findById_WithValidId_ShouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(1L);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertTrue(response.getNotifyHigh());
        assertFalse(response.getNotifyMedium());
    }

    @Test
    void findById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void update_WithValidRequestAndNewPassword_ShouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newEncodedPassword");

        userService.update(1L, updateRequest);

        assertEquals("Marcus Updated", user.getName());
        assertEquals("marcus.new@user.com", user.getEmail());
        assertEquals("newEncodedPassword", user.getPassword());
        assertFalse(user.getNotifyHigh());
        assertTrue(user.getNotifyMedium());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_WithValidRequestAndEmptyPassword_ShouldUpdateUserWithoutPasswordChange() {
        updateRequest.setPassword("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.update(1L, updateRequest);

        assertEquals("Marcus Updated", user.getName());
        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(99L, updateRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_WithValidId_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void requestReset_WithValidEmail_ShouldCreateTokenAndSendEmail() {
        when(userRepository.findByEmail("marcus@user.com")).thenReturn(Optional.of(user));

        userService.requestReset("marcus@user.com");

        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void requestReset_WithInvalidEmail_ShouldThrowResourceNotFoundException() {
        when(userRepository.findByEmail("invalid@user.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.requestReset("invalid@user.com"));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void resetPassword_WithValidToken_ShouldChangePasswordAndDeleteToken() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("validToken");
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(passwordResetTokenRepository.findByToken("validToken")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("newEncodedPassword");

        userService.resetPassword("validToken", "NewPassword@123");

        assertEquals("newEncodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldThrowRuntimeException() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("expiredToken");
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().minusMinutes(10));

        when(passwordResetTokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(resetToken));

        assertThrows(RuntimeException.class, () -> userService.resetPassword("expiredToken", "NewPassword@123"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldThrowRuntimeException() {
        when(passwordResetTokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.resetPassword("invalidToken", "NewPassword@123"));
    }

    @Test
    void resetPassword_WithWeakPassword_ShouldThrowWeakPasswordException() {
        assertThrows(WeakPasswordException.class, () -> userService.resetPassword("validToken", "weak"));
        verify(passwordResetTokenRepository, never()).findByToken(anyString());
    }

    @Test
    void update_WithWeakPassword_ShouldThrowWeakPasswordException() {
        updateRequest.setPassword("weak");
        assertThrows(WeakPasswordException.class, () -> userService.update(1L, updateRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
