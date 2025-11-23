package com.aissummarizer.jennet.common.validator;

import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.document.tools.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 50_000_000; // 50MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "application/pdf"
    );

    /**
     * Validate uploaded file
     * @param file File to validate
     * @throws InvalidFileException if validation fails
     */
    public void     validate(MultipartFile file) throws InvalidFileException {
        validateNotNull(file);
        validateNotEmpty(file);
        validateSize(file);
        validateFilename(file);
        // Content type validation is optional - file extension is more reliable
    }

    private void validateNotNull(MultipartFile file) throws InvalidFileException {
        if (file == null) {
            throw new InvalidFileException("File cannot be null");
        }
    }

    private void validateNotEmpty(MultipartFile file) throws InvalidFileException {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }
    }

    private void validateSize(MultipartFile file) throws InvalidFileException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    String.format("File size (%s) exceeds maximum allowed size (%s)",
                            FileUtils.formatFileSize(file.getSize()),
                            FileUtils.formatFileSize(MAX_FILE_SIZE))
            );
        }

        if (file.getSize() == 0) {
            throw new InvalidFileException("File size is 0 bytes");
        }
    }

    private void validateFilename(MultipartFile file) throws InvalidFileException {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("Filename is missing");
        }

        if (!filename.contains(".")) {
            throw new InvalidFileException("Filename must have an extension");
        }

        // Check for path traversal attempts
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new InvalidFileException("Invalid filename: contains illegal characters");
        }
    }
}