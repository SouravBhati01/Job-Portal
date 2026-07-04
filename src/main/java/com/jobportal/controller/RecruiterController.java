package com.jobportal.controller;

import com.jobportal.dto.request.ApplicationStatusRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruiter")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_RECRUITER')")
@Tag(name = "Recruiter Module", description = "Post Jobs · View Applicants · Manage Hiring")
public class RecruiterController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    // ── Post Jobs ──────────────────────────────────────────────────────

    @GetMapping("/jobs")
    @Operation(summary = "View all jobs I have posted")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> getMyJobs(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                jobService.getMyJobs(auth.getName(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    // ── View Applicants ────────────────────────────────────────────────

    @GetMapping("/jobs/{jobId}/applicants")
    @Operation(summary = "View all applicants for a specific job")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponse>>> viewApplicants(
            @PathVariable Long jobId,
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationsForJob(jobId, auth.getName(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "View a specific application detail")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplication(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationById(id, auth.getName())));
    }

    // ── Manage Hiring ──────────────────────────────────────────────────

    @PatchMapping("/applications/{id}/status")
    @Operation(summary = "Update application status: UNDER_REVIEW | SHORTLISTED | INTERVIEW_SCHEDULED | OFFERED | REJECTED")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateHiringStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Application status updated",
                applicationService.updateStatus(id, request, auth.getName())));
    }
}
