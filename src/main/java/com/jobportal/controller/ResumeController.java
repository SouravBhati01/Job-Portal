package com.jobportal.controller;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ResumeResponse;
import com.jobportal.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Resume Module", description = "Upload Resume · View · Download · Set Primary · Delete")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    @Operation(summary = "Upload a resume (PDF / DOC / DOCX, max 5MB)")
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        ResumeResponse resume = resumeService.upload(file, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded successfully", resume));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    @Operation(summary = "List all my uploaded resumes")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getMyResumes(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(resumeService.getMyResumes(auth.getName())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_APPLICANT') or hasRole('ROLE_RECRUITER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get resume metadata by ID")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResume(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(resumeService.getResumeById(id, auth.getName())));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('ROLE_APPLICANT') or hasRole('ROLE_RECRUITER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Download a resume file")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id,
            Authentication auth) {
        Resource resource = resumeService.downloadResume(id, auth.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PatchMapping("/{id}/primary")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    @Operation(summary = "Set a resume as primary/default")
    public ResponseEntity<ApiResponse<ResumeResponse>> setPrimary(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Primary resume updated",
                resumeService.setPrimary(id, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    @Operation(summary = "Delete a resume")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @PathVariable Long id,
            Authentication auth) {
        resumeService.deleteResume(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Resume deleted", null));
    }
}
