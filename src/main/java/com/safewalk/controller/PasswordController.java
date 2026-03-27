package com.safewalk.controller;

import com.safewalk.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final UserServiceImpl userService;

    @PostMapping("/forgot")
    public ResponseEntity<Void> forgot(@RequestParam String email) {
        userService.requestReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset(
            @RequestParam String token,
            @RequestParam String password
    ) {
        userService.resetPassword(token, password);
        return ResponseEntity.ok().build();
    }
}
