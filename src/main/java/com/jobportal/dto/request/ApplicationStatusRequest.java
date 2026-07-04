package com.jobportal.dto.request;

import com.jobportal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
