package com.safewalk.service.impl;

import com.safewalk.dto.UserResponse;
import com.safewalk.dto.UserUpdateRequest;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.exception.UnauthorizedException;
import com.safewalk.model.User;
import com.safewalk.repository.UserRepository;
import com.safewalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return mapToResponse(user);
    }

    @Override
    public void update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setNotifyHigh(request.getNotifyHigh());
        user.setNotifyMedium(request.getNotifyMedium());
        user.setNotifyLow(request.getNotifyLow());

        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.getId().equals(id)) {
            throw new UnauthorizedException("Você não tem permissão para deletar este Usuario");
        }

        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .notifyHigh(user.getNotifyHigh())
                .notifyMedium(user.getNotifyMedium())
                .notifyLow(user.getNotifyLow())
                .build();
    }
}
