package com.jobportal.service;

import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import org.springframework.data.domain.Pageable;

public interface JobService {

    // Job Module
    JobResponse createJob(JobRequest request, String recruiterEmail);
    JobResponse getJobById(Long id);
    PagedResponse<JobResponse> searchJobs(String keyword, String location, JobType type,
                                           Boolean remote, Double salaryMin, Pageable pageable);
    JobResponse updateJob(Long id, JobRequest request, String recruiterEmail);
    JobResponse updateStatus(Long id, JobStatus status, String recruiterEmail);
    void deleteJob(Long id, String requesterEmail);

    // Recruiter
    PagedResponse<JobResponse> getMyJobs(String recruiterEmail, Pageable pageable);

    // Admin
    PagedResponse<JobResponse> getAllJobsAdmin(Pageable pageable);
    JobResponse moderateJob(Long id, JobStatus status);
}
