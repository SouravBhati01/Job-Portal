package com.jobportal.repository;

import com.jobportal.entity.JobApplication;
import com.jobportal.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findAllByApplicantId(Long applicantId, Pageable pageable);

    Page<JobApplication> findAllByJobId(Long jobId, Pageable pageable);

    Page<JobApplication> findAllByJobIdAndStatus(Long jobId, ApplicationStatus status, Pageable pageable);

    Optional<JobApplication> findByApplicantIdAndJobId(Long applicantId, Long jobId);

    boolean existsByApplicantIdAndJobId(Long applicantId, Long jobId);

    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.job.id = :jobId")
    long countByJobId(@Param("jobId") Long jobId);

    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.applicant.id = :applicantId AND a.status = :status")
    long countByApplicantIdAndStatus(@Param("applicantId") Long applicantId,
                                     @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.job.postedBy.id = :recruiterId")
    long countByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.status = :status")
    long countByStatus(@Param("status") ApplicationStatus status);
}
