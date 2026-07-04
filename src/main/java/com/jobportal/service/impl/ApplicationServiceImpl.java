package com.jobportal.service.impl;

import com.jobportal.dto.request.ApplicationRequest;
import com.jobportal.dto.request.ApplicationStatusRequest;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.enums.JobStatus;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final JobApplicationRepository appRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional
    public ApplicationResponse apply(ApplicationRequest req, String applicantEmail) {
        User applicant = findUserByEmail(applicantEmail);
        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", req.getJobId()));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new BadRequestException("Job is not accepting applications (status: " + job.getStatus() + ")");
        }
        if (appRepository.existsByApplicantIdAndJobId(applicant.getId(), job.getId())) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        Resume resume = null;
        if (req.getResumeId() != null) {
            resume = resumeRepository.findByIdAndOwnerId(req.getResumeId(), applicant.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", req.getResumeId()));
        }

        JobApplication app = JobApplication.builder()
                .applicant(applicant)
                .job(job)
                .resume(resume)
                .coverLetter(req.getCoverLetter())
                .proposedSalary(req.getProposedSalary())
                .status(ApplicationStatus.APPLIED)
                .build();

        app = appRepository.save(app);
        log.info("User {} applied for job {}", applicantEmail, job.getId());
        return toResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getMyApplications(String applicantEmail, Pageable pageable) {
        User applicant = findUserByEmail(applicantEmail);
        return PagedResponse.from(
                appRepository.findAllByApplicantId(applicant.getId(), pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id, String requesterEmail) {
        JobApplication app = findById(id);
        User requester = findUserByEmail(requesterEmail);

        // Validate references are not null
        if (app.getApplicant() == null) {
            throw new ResourceNotFoundException("Applicant not found for this application", "applicationId", id);
        }
        if (app.getJob() == null || app.getJob().getPostedBy() == null) {
            throw new ResourceNotFoundException("Job or job poster not found for this application", "applicationId", id);
        }
        
        boolean isApplicant = app.getApplicant().getId().equals(requester.getId());
        boolean isJobOwner = app.getJob().getPostedBy().getId().equals(requester.getId());

        if (!isApplicant && !isJobOwner) {
            throw new UnauthorizedException("Access denied to this application");
        }
        return toResponse(app);
    }

    @Override
    @Transactional
    public void withdraw(Long id, String applicantEmail) {
        JobApplication app = findById(id);
        User applicant = findUserByEmail(applicantEmail);

        if (!app.getApplicant().getId().equals(applicant.getId())) {
            throw new UnauthorizedException("You cannot withdraw this application");
        }
        if (app.getStatus() == ApplicationStatus.OFFERED
                || app.getStatus() == ApplicationStatus.REJECTED
                || app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot withdraw application with current status: " + app.getStatus());
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
        appRepository.save(app);
        log.info("Application {} withdrawn by {}", id, applicantEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getApplicationsForJob(Long jobId,
                                                                     String recruiterEmail,
                                                                     Pageable pageable) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        User recruiter = findUserByEmail(recruiterEmail);

        // Validate job poster is not null
        if (job.getPostedBy() == null) {
            throw new ResourceNotFoundException("Job poster not found for this job", "jobId", jobId);
        }
        
        if (!job.getPostedBy().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You do not own this job");
        }
        return PagedResponse.from(appRepository.findAllByJobId(jobId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatusRequest req, String recruiterEmail) {
        if (req == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        
        JobApplication app = findById(id);
        User recruiter = findUserByEmail(recruiterEmail);

        // Validate postedBy is not null
        if (app.getJob().getPostedBy() == null) {
            throw new ResourceNotFoundException("Job poster not found for application", "applicationId", id);
        }
        
        if (!app.getJob().getPostedBy().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You do not have permission to update this application");
        }
        app.setStatus(req.getStatus());
        if (req.getNotes() != null) {
            app.setRecruiterNotes(req.getNotes());
        }
        return toResponse(appRepository.save(app));
    }

    private JobApplication findById(Long id) {
        return appRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private ApplicationResponse toResponse(JobApplication a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getTitle())
                .jobLocation(a.getJob().getLocation())
                .applicantId(a.getApplicant().getId())
                .applicantName(a.getApplicant().getFullName())
                .applicantEmail(a.getApplicant().getEmail())
                .resumeId(a.getResume() != null ? a.getResume().getId() : null)
                .resumeUrl(a.getResume() != null ? a.getResume().getFileUrl() : null)
                .coverLetter(a.getCoverLetter())
                .status(a.getStatus())
                .recruiterNotes(a.getRecruiterNotes())
                .proposedSalary(a.getProposedSalary())
                .appliedAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
