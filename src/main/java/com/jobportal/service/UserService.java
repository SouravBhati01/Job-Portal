package com.jobportal.service;

import com.jobportal.dto.response.PagedResponse;
import com.jobportal.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getMe(String email);
    PagedResponse<UserResponse> getAllUsers(String search, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse toggleUserStatus(Long id);
    void deleteUser(Long id);
}
