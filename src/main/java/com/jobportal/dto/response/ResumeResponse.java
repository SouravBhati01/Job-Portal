package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResumeResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String originalFileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private boolean primary;
    private LocalDateTime uploadedAt;
}
