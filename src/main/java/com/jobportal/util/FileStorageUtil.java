package com.jobportal.util;

import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class FileStorageUtil {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final Path uploadDir;

    public FileStorageUtil(@Value("${app.upload.dir:uploads/resumes}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Upload directory initialized: {}", this.uploadDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public String store(MultipartFile file) {
        validateFile(file);

        String original = org.springframework.util.StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume");

        String ext = extractExtension(original);
        String stored = UUID.randomUUID() + ext;

        try {
            Path target = uploadDir.resolve(stored);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {}", stored);
            return stored;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + original, e);
        }
    }

    public void delete(String storedFileName) {
        try {
            Path file = uploadDir.resolve(storedFileName);
            Files.deleteIfExists(file);
            log.info("Deleted file: {}", storedFileName);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", storedFileName, e.getMessage());
        }
    }

    public Path resolve(String storedFileName) {
        if (storedFileName == null || storedFileName.contains("..") || storedFileName.startsWith("/")) {
            throw new FileStorageException("Invalid file path");
        }
        Path resolved = uploadDir.resolve(storedFileName).normalize();
        // Verify the resolved path is still within uploadDir (prevents path traversal attacks)
        if (!resolved.startsWith(uploadDir)) {
            throw new FileStorageException("Invalid file path: access denied");
        }
        return resolved;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException(
                    "Invalid file type. Only PDF and Word documents (.pdf, .doc, .docx) are accepted");
        }
        try {
            // Validate file content using magic numbers (prevents content type spoofing)
            validateFileContent(file);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file content for validation", e);
        }
    }

    private void validateFileContent(MultipartFile file) throws IOException {
        byte[] header = new byte[4];
        try (var is = file.getInputStream()) {
            is.read(header);
        }
        
        // Check file signatures (magic numbers)
        boolean isValidPDF = header[0] == (byte) 0x25 && header[1] == (byte) 0x50 
                             && header[2] == (byte) 0x44 && header[3] == (byte) 0x46; // %PDF
        
        boolean isValidOffice = header[0] == (byte) 0x50 && header[1] == (byte) 0x4B 
                                && header[2] == (byte) 0x03 && header[3] == (byte) 0x04; // PK..
        
        if (!isValidPDF && !isValidOffice) {
            throw new BadRequestException("File content validation failed. File appears corrupted or not a valid document.");
        }
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : "";
    }
}
