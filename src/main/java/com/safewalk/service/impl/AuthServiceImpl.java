package com.safewalk.service.impl;

import com.safewalk.dto.AuthRequest;
import com.safewalk.dto.AuthResponse;
import com.safewalk.dto.SignUpRequest;
import com.safewalk.dto.UserResponse;
import com.safewalk.exception.EmailAlreadyExistsException;
import com.safewalk.exception.InvalidCredentialsException;
import com.safewalk.exception.InactiveUserException;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.model.User;
import com.safewalk.model.EmailActivationToken;
import com.safewalk.repository.UserRepository;
import com.safewalk.repository.EmailActivationTokenRepository;
import com.safewalk.security.JwtUtil;
import com.safewalk.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailActivationTokenRepository emailActivationTokenRepository;
    private final JavaMailSender mailSender;

    @Value("${backend.url}")
    private String backendUrl;

    @Transactional
    @Override
    public AuthResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Este e-mail já está cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .build();

        user = userRepository.save(user);

        String activationTokenValue = UUID.randomUUID().toString();
        EmailActivationToken activationToken = new EmailActivationToken();
        activationToken.setToken(activationTokenValue);
        activationToken.setUser(user);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailActivationTokenRepository.save(activationToken);

        String link = backendUrl + "/api/auth/activate?token=" + activationTokenValue;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ativação de conta - SafeWalk");
        message.setText("Olá " + user.getName() + ",\n\nClique no link abaixo para ativar sua conta no SafeWalk:\n" + link);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.info("Erro ao tentar enviar e-mail ao usuário.");
        }

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        return AuthResponse.builder()
                .token(null)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha incorretos"));

        if (user.getDeletedAt() != null) {
            throw new InvalidCredentialsException("E-mail ou senha incorretos");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("E-mail ou senha incorretos");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new InactiveUserException("Sua conta ainda não foi ativada. Verifique seu e-mail para ativar sua conta.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Transactional
    @Override
    public void activateAccount(String token) {
        EmailActivationToken activationToken = emailActivationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de ativação inválido"));

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            emailActivationTokenRepository.delete(activationToken);
            throw new IllegalArgumentException("Token de ativação expirado");
        }

        User user = activationToken.getUser();
        user.setIsActive(true);
        userRepository.save(user);

        emailActivationTokenRepository.delete(activationToken);
    }
}
