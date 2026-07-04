package com.jobportal.entity;

import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_title", columnList = "title"),
        @Index(name = "idx_job_location", columnList = "location"),
        @Index(name = "idx_job_posted_by", columnList = "posted_by_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(length = 150)
    private String location;

    @Column(nullable = false)
    @Builder.Default
    private boolean remote = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private JobType jobType = JobType.FULL_TIME;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    @Column(length = 100)
    private String experienceLevel;

    private Double salaryMin;
    private Double salaryMax;

    @Column(length = 3)
    @Builder.Default
    private String salaryCurrency = "USD";

    @Column(length = 100)
    private String category;

    private Integer vacancies;

    private LocalDate applicationDeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id", nullable = false)
    private User postedBy;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobApplication> applications = new ArrayList<>();

    public int getApplicationCount() {
        return applications.size();
    }
}
