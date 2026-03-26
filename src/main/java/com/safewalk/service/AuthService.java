package com.safewalk.service;

import com.safewalk.dto.AuthRequest;
import com.safewalk.dto.AuthResponse;
import com.safewalk.dto.SignUpRequest;

public interface AuthService {

    AuthResponse signup(SignUpRequest request);

    AuthResponse login(AuthRequest request);
}
