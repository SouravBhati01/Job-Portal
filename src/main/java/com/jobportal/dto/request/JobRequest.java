package com.jobportal.dto.request;

import com.jobportal.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 200, message = "Title must be 3-200 characters")
    private String title;

    @Size(max = 150, message = "Company name must be at most 150 characters")
    private String company;

    @NotBlank(message = "Job description is required")
    @Size(min = 30, message = "Description must be at least 30 characters")
    private String description;

    private String requirements;
    private String benefits;

    @Size(max = 150, message = "Location must be at most 150 characters")
    private String location;

    private boolean remote;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @Size(max = 50, message = "Experience level must be at most 50 characters")
    private String experienceLevel;

    @PositiveOrZero(message = "Minimum salary must be zero or positive")
    private Double salaryMin;

    @PositiveOrZero(message = "Maximum salary must be zero or positive")
    private Double salaryMax;

    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters (e.g. USD)")
    private String salaryCurrency;

    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;

    @Min(value = 1, message = "Vacancies must be at least 1")
    private Integer vacancies;

    @Future(message = "Application deadline must be a future date")
    private LocalDate applicationDeadline;
}
