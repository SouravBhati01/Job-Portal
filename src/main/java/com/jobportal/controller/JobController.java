package com.jobportal.controller;

import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import com.jobportal.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Module", description = "Create Job · Update Job · Delete Job · Search Jobs")
public class JobController {

    private final JobService jobService;

    // ── PUBLIC ─────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Search active jobs — public, no token needed")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Double salaryMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ResponseEntity.ok(ApiResponse.success(
                jobService.searchJobs(keyword, location, jobType, remote, salaryMin,
                        PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID — public")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(id)));
    }

    // ── RECRUITER ──────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Post a new job listing (Recruiter)")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            Authentication auth,
            @Valid @RequestBody JobRequest request) {
        JobResponse job = jobService.createJob(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job posted successfully", job));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a job listing (Recruiter, owner only)")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            Authentication auth,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Job updated",
                jobService.updateJob(id, request, auth.getName())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change job status: ACTIVE | CLOSED | DRAFT (Recruiter, owner only)")
    public ResponseEntity<ApiResponse<JobResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                jobService.updateStatus(id, status, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a job listing (Recruiter owner or Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id,
            Authentication auth) {
        jobService.deleteJob(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Job deleted", null));
    }
}
