package com.jobportal.service;

import com.jobportal.dto.response.ResumeResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    ResumeResponse upload(MultipartFile file, String ownerEmail);
    List<ResumeResponse> getMyResumes(String ownerEmail);
    ResumeResponse getResumeById(Long id, String requesterEmail);
    ResumeResponse setPrimary(Long id, String ownerEmail);
    void deleteResume(Long id, String ownerEmail);
    Resource downloadResume(Long id, String requesterEmail);
}
