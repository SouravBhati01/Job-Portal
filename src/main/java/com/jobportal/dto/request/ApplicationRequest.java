package com.jobportal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private Long resumeId;

    @Size(max = 3000, message = "Cover letter must not exceed 3000 characters")
    private String coverLetter;

    @PositiveOrZero(message = "Proposed salary must be zero or positive")
    private Double proposedSalary;
}
