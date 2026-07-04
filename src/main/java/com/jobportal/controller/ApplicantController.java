package com.jobportal.controller;

import com.jobportal.dto.request.ApplicationRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.service.ApplicationService;
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
@RequestMapping("/applicant")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_APPLICANT')")
@Tag(name = "Applicant Module", description = "Apply Job · Upload Resume · Track Applications")
public class ApplicantController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            Authentication auth,
            @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse app = applicationService.apply(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", app));
    }

    @GetMapping("/applications")
    @Operation(summary = "Track all my job applications")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponse>>> trackApplications(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getMyApplications(auth.getName(), pageable)));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get details of one application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplication(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationById(id, auth.getName())));
    }

    @PatchMapping("/applications/{id}/withdraw")
    @Operation(summary = "Withdraw a pending application")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @PathVariable Long id,
            Authentication auth) {
        applicationService.withdraw(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully", null));
    }
}
