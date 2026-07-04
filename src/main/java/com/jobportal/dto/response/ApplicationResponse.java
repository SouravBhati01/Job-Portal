package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jobportal.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String jobLocation;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private Long resumeId;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private String recruiterNotes;
    private Double proposedSalary;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
