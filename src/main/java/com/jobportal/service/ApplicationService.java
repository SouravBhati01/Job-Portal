package com.jobportal.service;

import com.jobportal.dto.request.ApplicationRequest;
import com.jobportal.dto.request.ApplicationStatusRequest;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {

    // Applicant Module
    ApplicationResponse apply(ApplicationRequest request, String applicantEmail);
    PagedResponse<ApplicationResponse> getMyApplications(String applicantEmail, Pageable pageable);
    ApplicationResponse getApplicationById(Long id, String requesterEmail);
    void withdraw(Long id, String applicantEmail);

    // Recruiter Module
    PagedResponse<ApplicationResponse> getApplicationsForJob(Long jobId, String recruiterEmail, Pageable pageable);
    ApplicationResponse updateStatus(Long id, ApplicationStatusRequest req, String recruiterEmail);
}
