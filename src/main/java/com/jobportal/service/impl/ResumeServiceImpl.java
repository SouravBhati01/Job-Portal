package com.jobportal.service.impl;

import com.jobportal.dto.response.ResumeResponse;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.enums.RoleName;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.FileStorageException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ResumeService;
import com.jobportal.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final int MAX_RESUMES_PER_USER = 5;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorage;

    @Override
    @Transactional
    public ResumeResponse upload(MultipartFile file, String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);

        if (resumeRepository.countByOwnerId(owner.getId()) >= MAX_RESUMES_PER_USER) {
            throw new BadRequestException(
                    "Maximum of " + MAX_RESUMES_PER_USER + " resumes allowed. Delete an existing one first.");
        }

        String stored = fileStorage.store(file);
        boolean isFirst = resumeRepository.countByOwnerId(owner.getId()) == 0;

        Resume resume = Resume.builder()
                .owner(owner)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(stored)
                .fileUrl("/api/resumes/" + stored + "/download")
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .primary(isFirst)
                .build();

        return toResponse(resumeRepository.save(resume));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponse> getMyResumes(String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);
        return resumeRepository.findAllByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long id, String requesterEmail) {
        Resume resume = findById(id);
        User requester = findUserByEmail(requesterEmail);

        boolean isOwner = resume.getOwner().getId().equals(requester.getId());
        boolean isRecruiterOrAdmin = requester.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_RECRUITER
                        || r.getName() == RoleName.ROLE_ADMIN);

        if (!isOwner && !isRecruiterOrAdmin) {
            throw new UnauthorizedException("Access denied to this resume");
        }
        return toResponse(resume);
    }

    @Override
    @Transactional
    public ResumeResponse setPrimary(Long id, String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);
        Resume resume = resumeRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        resumeRepository.clearPrimaryForOwner(owner.getId());
        resume.setPrimary(true);
        return toResponse(resumeRepository.save(resume));
    }

    @Override
    @Transactional
    public void deleteResume(Long id, String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);
        Resume resume = resumeRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        fileStorage.delete(resume.getStoredFileName());
        resumeRepository.delete(resume);
        log.info("Deleted resume {} for user {}", id, ownerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadResume(Long id, String requesterEmail) {
        Resume resume = findById(id);
        User requester = findUserByEmail(requesterEmail);

        boolean isOwner = resume.getOwner().getId().equals(requester.getId());
        boolean isRecruiterOrAdmin = requester.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_RECRUITER
                        || r.getName() == RoleName.ROLE_ADMIN);

        if (!isOwner && !isRecruiterOrAdmin) {
            throw new UnauthorizedException("Access denied");
        }

        try {
            Path path = fileStorage.resolve(resume.getStoredFileName());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new FileStorageException("File not found or not readable: " + resume.getOriginalFileName());
        } catch (MalformedURLException e) {
            throw new FileStorageException("Invalid file path for resume: " + id, e);
        }
    }

    private Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private ResumeResponse toResponse(Resume r) {
        return ResumeResponse.builder()
                .id(r.getId())
                .ownerId(r.getOwner().getId())
                .ownerName(r.getOwner().getFullName())
                .originalFileName(r.getOriginalFileName())
                .fileUrl(r.getFileUrl())
                .contentType(r.getContentType())
                .fileSize(r.getFileSize())
                .primary(r.isPrimary())
                .uploadedAt(r.getCreatedAt())
                .build();
    }
}
