package com.safewalk.controller;

import com.safewalk.dto.AuthRequest;
import com.safewalk.dto.AuthResponse;
import com.safewalk.dto.SignUpRequest;
import com.safewalk.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @Value("${app.expo.mock}")
    private boolean isExpoMock;

    @Value("${app.expo.url}")
    private String expoUrl;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activate(@RequestParam String token) {
        try {
            authService.activateAccount(token);
            String redirectUrl = isExpoMock ? (expoUrl + "activate?status=success") : "safewalk://activate?status=success";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            String encodedMessage = java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
            String redirectUrl = isExpoMock ? (expoUrl + "activate?status=error&message=" + encodedMessage) : ("safewalk://activate?status=error&message=" + encodedMessage);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
    }
}

