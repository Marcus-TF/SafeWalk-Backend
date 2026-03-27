package com.safewalk.service;

import com.safewalk.dto.UserResponse;
import com.safewalk.dto.UserUpdateRequest;

public interface UserService {

    UserResponse findById(Long userId);

    void update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
