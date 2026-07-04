package com.jobportal.service.impl;

import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PagedResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import com.jobportal.enums.RoleName;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest req, String recruiterEmail) {
        User recruiter = findUserByEmail(recruiterEmail);
        validateSalaryRange(req.getSalaryMin(), req.getSalaryMax());

        Job job = Job.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .requirements(req.getRequirements())
                .benefits(req.getBenefits())
                .location(req.getLocation())
                .remote(req.isRemote())
                .jobType(req.getJobType())
                .status(JobStatus.DRAFT)
                .experienceLevel(req.getExperienceLevel())
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .salaryCurrency(req.getSalaryCurrency() != null ? req.getSalaryCurrency() : "USD")
                .category(req.getCategory())
                .vacancies(req.getVacancies())
                .applicationDeadline(req.getApplicationDeadline())
                .postedBy(recruiter)
                .build();

        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> searchJobs(String keyword, String location,
                                                  JobType type, Boolean remote,
                                                  Double salaryMin, Pageable pageable) {
        return PagedResponse.from(
                jobRepository.searchActiveJobs(keyword, location, type, remote, salaryMin, pageable)
                             .map(this::toResponse));
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest req, String recruiterEmail) {
        Job job = findById(id);
        User recruiter = findUserByEmail(recruiterEmail);
        assertOwner(job, recruiter);
        validateSalaryRange(req.getSalaryMin(), req.getSalaryMax());

        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setRequirements(req.getRequirements());
        job.setBenefits(req.getBenefits());
        job.setLocation(req.getLocation());
        job.setRemote(req.isRemote());
        job.setJobType(req.getJobType());
        job.setExperienceLevel(req.getExperienceLevel());
        job.setSalaryMin(req.getSalaryMin());
        job.setSalaryMax(req.getSalaryMax());
        if (req.getSalaryCurrency() != null) job.setSalaryCurrency(req.getSalaryCurrency());
        job.setCategory(req.getCategory());
        job.setVacancies(req.getVacancies());
        job.setApplicationDeadline(req.getApplicationDeadline());

        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse updateStatus(Long id, JobStatus status, String recruiterEmail) {
        Job job = findById(id);
        User recruiter = findUserByEmail(recruiterEmail);
        assertOwner(job, recruiter);
        job.setStatus(status);
        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long id, String requesterEmail) {
        Job job = findById(id);
        User requester = findUserByEmail(requesterEmail);
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
        if (!isAdmin) {
            assertOwner(job, requester);
        }
        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getMyJobs(String recruiterEmail, Pageable pageable) {
        User recruiter = findUserByEmail(recruiterEmail);
        return PagedResponse.from(
                jobRepository.findAllByPostedById(recruiter.getId(), pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getAllJobsAdmin(Pageable pageable) {
        return PagedResponse.from(jobRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    @Transactional
    public JobResponse moderateJob(Long id, JobStatus status) {
        Job job = findById(id);
        job.setStatus(status);
        return toResponse(jobRepository.save(job));
    }

    private void assertOwner(Job job, User user) {
        if (!job.getPostedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to modify this job");
        }
    }

    private void validateSalaryRange(Double min, Double max) {
        if (min != null && max != null && min > max) {
            throw new BadRequestException("Minimum salary cannot exceed maximum salary");
        }
    }

    private Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    JobResponse toResponse(Job j) {
        return JobResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .description(j.getDescription())
                .requirements(j.getRequirements())
                .benefits(j.getBenefits())
                .location(j.getLocation())
                .remote(j.isRemote())
                .jobType(j.getJobType())
                .status(j.getStatus())
                .experienceLevel(j.getExperienceLevel())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .salaryCurrency(j.getSalaryCurrency())
                .category(j.getCategory())
                .vacancies(j.getVacancies())
                .applicationDeadline(j.getApplicationDeadline())
                .applicationCount(j.getApplicationCount())
                .recruiterId(j.getPostedBy().getId())
                .recruiterName(j.getPostedBy().getFullName())
                .recruiterEmail(j.getPostedBy().getEmail())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }
}
