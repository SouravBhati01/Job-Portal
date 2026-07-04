package com.jobportal.repository;

import com.jobportal.entity.Job;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAllByPostedById(Long recruiterId, Pageable pageable);

    Page<Job> findAllByStatus(JobStatus status, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "  LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(j.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR " +
           "  LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:remote IS NULL OR j.remote = :remote) AND " +
           "(:salaryMin IS NULL OR j.salaryMin >= :salaryMin)")
    Page<Job> searchActiveJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") JobType jobType,
            @Param("remote") Boolean remote,
            @Param("salaryMin") Double salaryMin,
            Pageable pageable);

    long countByStatus(JobStatus status);

    long countByPostedById(Long recruiterId);
}
