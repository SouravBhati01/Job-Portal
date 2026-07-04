package com.jobportal.entity;

import com.jobportal.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_app_applicant", columnList = "applicant_id"),
        @Index(name = "idx_app_job", columnList = "job_id"),
        @Index(name = "idx_app_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_applicant_job", columnNames = {"applicant_id", "job_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(columnDefinition = "TEXT")
    private String recruiterNotes;

    private Double proposedSalary;
}
