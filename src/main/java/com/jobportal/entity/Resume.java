package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_resume_owner", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 255)
    private String storedFileName;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false, length = 50)
    private String contentType;

    private Long fileSize;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = false;
}
