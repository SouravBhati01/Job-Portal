package com.jobportal.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReportResponse {

    // Users
    private long totalUsers;
    private long totalApplicants;
    private long totalRecruiters;
    private long activeUsers;
    private long disabledUsers;

    // Jobs
    private long totalJobs;
    private long activeJobs;
    private long closedJobs;
    private long flaggedJobs;
    private long draftJobs;

    // Applications
    private long totalApplications;
    private long applicationsApplied;
    private long applicationsShortlisted;
    private long applicationsOffered;
    private long applicationsRejected;

    // Resumes
    private long totalResumes;
}
