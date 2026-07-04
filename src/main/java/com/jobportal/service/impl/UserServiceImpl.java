package com.jobportal.service.impl;

import com.jobportal.dto.response.PagedResponse;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(String email) {
        return toResponse(findByEmail(email));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return PagedResponse.from(userRepository.searchUsers(search, pageable).map(this::toResponse));
        }
        return PagedResponse.from(userRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .roles(u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
