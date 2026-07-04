package com.jobportal.controller;

import com.jobportal.dto.response.AdminReportResponse;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.enums.JobStatus;
import com.jobportal.service.AdminService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin Module", description = "User Management · Job Moderation · Reports")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final AdminService adminService;

    // ── User Management ────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "List all users with optional search")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                userService.getAllUsers(search,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PatchMapping("/users/{id}/toggle")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User status toggled",
                userService.toggleUserStatus(id)));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Permanently delete a user account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    // ── Job Moderation ─────────────────────────────────────────────────

    @GetMapping("/jobs")
    @Operation(summary = "List all jobs (all statuses) for moderation")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> listAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                jobService.getAllJobsAdmin(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @PatchMapping("/jobs/{id}/moderate")
    @Operation(summary = "Moderate a job: set to ACTIVE | CLOSED | FLAGGED | EXPIRED")
    public ResponseEntity<ApiResponse<JobResponse>> moderateJob(
            @PathVariable Long id,
            @RequestParam JobStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Job status set to " + status,
                jobService.moderateJob(id, status)));
    }

    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Remove a job listing permanently")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id,
            Authentication auth) {
        jobService.deleteJob(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Job removed", null));
    }

    // ── Reports ────────────────────────────────────────────────────────

    @GetMapping("/reports")
    @Operation(summary = "Generate platform-wide statistics report")
    public ResponseEntity<ApiResponse<AdminReportResponse>> report() {
        return ResponseEntity.ok(ApiResponse.success(adminService.generateReport()));
    }
}
