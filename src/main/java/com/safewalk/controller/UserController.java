package com.safewalk.controller;

import com.safewalk.dto.UserResponse;
import com.safewalk.dto.UserUpdateRequest;
import com.safewalk.security.UserPrincipal;
import com.safewalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/findById")
    public UserResponse findById(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.findById(userPrincipal.getId());
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody UserUpdateRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.update(userPrincipal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.delete(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}
