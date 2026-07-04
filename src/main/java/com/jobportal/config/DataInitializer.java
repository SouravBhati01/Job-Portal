package com.jobportal.config;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.enums.RoleName;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Step 1: Ensure all three roles exist in the roles table
            Role adminRole = ensureRole(RoleName.ROLE_ADMIN);
            Role recruiterRole = ensureRole(RoleName.ROLE_RECRUITER);
            Role applicantRole = ensureRole(RoleName.ROLE_APPLICANT);
            log.info("Roles initialised: ROLE_ADMIN, ROLE_RECRUITER, ROLE_APPLICANT");

            // Step 2: Seed the admin account
            if (!userRepository.existsByEmail("admin@jobportal.com")) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("Portal")
                        .email("admin@jobportal.com")
                        .password(passwordEncoder.encode("Admin@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(adminRole)))
                        .build();
                userRepository.save(admin);
                log.info("Admin account created  →  admin@jobportal.com  /  Admin@1234");
            }

            // Step 3: Seed a demo recruiter
            if (!userRepository.existsByEmail("recruiter@jobportal.com")) {
                User recruiter = User.builder()
                        .firstName("Demo")
                        .lastName("Recruiter")
                        .email("recruiter@jobportal.com")
                        .password(passwordEncoder.encode("Recruit@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(recruiterRole)))
                        .build();
                userRepository.save(recruiter);
                log.info("Demo recruiter created  →  recruiter@jobportal.com  /  Recruit@1234");
            }

            // Step 4: Seed a demo applicant
            if (!userRepository.existsByEmail("applicant@jobportal.com")) {
                User applicant = User.builder()
                        .firstName("Demo")
                        .lastName("Applicant")
                        .email("applicant@jobportal.com")
                        .password(passwordEncoder.encode("Apply@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(applicantRole)))
                        .build();
                userRepository.save(applicant);
                log.info("Demo applicant created  →  applicant@jobportal.com  /  Apply@1234");
            }
        };
    }

    private Role ensureRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}
