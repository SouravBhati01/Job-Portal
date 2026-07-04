package com.jobportal.service.impl;

import com.jobportal.dto.response.AdminReportResponse;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.RoleName;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository appRepository;
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminReportResponse generateReport() {
        long totalUsers = userRepository.count();
        long totalApplicants = userRepository.countByRoleName(RoleName.ROLE_APPLICANT);
        long totalRecruiters = userRepository.countByRoleName(RoleName.ROLE_RECRUITER);
        // Use optimized queries instead of loading all users
        long activeUsers = userRepository.countByEnabled(true);
        long disabledUsers = userRepository.countByEnabled(false);

        return AdminReportResponse.builder()
                // Users
                .totalUsers(totalUsers)
                .totalApplicants(totalApplicants)
                .totalRecruiters(totalRecruiters)
                .activeUsers(activeUsers)
                .disabledUsers(disabledUsers)
                // Jobs
                .totalJobs(jobRepository.count())
                .activeJobs(jobRepository.countByStatus(JobStatus.ACTIVE))
                .closedJobs(jobRepository.countByStatus(JobStatus.CLOSED))
                .flaggedJobs(jobRepository.countByStatus(JobStatus.FLAGGED))
                .draftJobs(jobRepository.countByStatus(JobStatus.DRAFT))
                // Applications
                .totalApplications(appRepository.count())
                .applicationsApplied(appRepository.countByStatus(ApplicationStatus.APPLIED))
                .applicationsShortlisted(appRepository.countByStatus(ApplicationStatus.SHORTLISTED))
                .applicationsOffered(appRepository.countByStatus(ApplicationStatus.OFFERED))
                .applicationsRejected(appRepository.countByStatus(ApplicationStatus.REJECTED))
                // Resumes
                .totalResumes(resumeRepository.count())
                .build();
    }
}
