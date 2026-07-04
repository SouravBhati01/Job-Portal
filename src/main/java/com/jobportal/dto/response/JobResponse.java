package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String requirements;
    private String benefits;
    private String location;
    private boolean remote;
    private JobType jobType;
    private JobStatus status;
    private String experienceLevel;
    private Double salaryMin;
    private Double salaryMax;
    private String salaryCurrency;
    private String category;
    private Integer vacancies;
    private LocalDate applicationDeadline;
    private int applicationCount;
    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
