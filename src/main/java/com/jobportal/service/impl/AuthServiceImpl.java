package com.jobportal.service.impl;

import com.jobportal.dto.request.LoginRequest;
import com.jobportal.dto.request.RegisterRequest;
import com.jobportal.dto.response.AuthResponse;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.enums.RoleName;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtTokenProvider;
import com.jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email '" + email + "' is already registered");
        }

        // Prevent self-registration as admin
        if (req.getRole() == RoleName.ROLE_ADMIN) {
            throw new BadRequestException("Admin accounts cannot be self-registered");
        }

        Role role = roleRepository.findByName(req.getRole())
                .orElseThrow(() -> new BadRequestException("Role not configured: " + req.getRole()));

        User user = User.builder()
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(email)
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .enabled(true)
                .roles(new java.util.HashSet<>(Set.of(role)))
                .build();

        user = userRepository.save(user);
        log.info("Registered new user: {} with role {}", email, req.getRole());

        return buildAuthResponse(user, tokenProvider.generateToken(email));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().toLowerCase().trim();

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "email", email));

        // Check if user account is disabled
        if (!user.isEnabled()) {
            log.warn("Login attempt by disabled user: {}", email);
            throw new UnauthorizedException("Your account has been disabled. Please contact support.");
        }

        String token = tokenProvider.generateToken(auth);
        log.info("Login successful: {}", email);

        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roleNames)
                .build();
    }
}
