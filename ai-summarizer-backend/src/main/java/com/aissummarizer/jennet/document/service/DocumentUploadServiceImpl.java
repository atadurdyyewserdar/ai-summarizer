package com.aissummarizer.jennet.document.service;

import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.common.validator.FileValidator;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.repository.DocumentUploadRepository;
import com.aissummarizer.jennet.document.tools.FileUtils;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of DocumentUploadService.
 * <p>
 * Handles:
 * - file validation
 * - file extension extraction
 * - saving document metadata in DB
 * - ensuring uploaded files can be extracted using the supported extractors
 */
@Service
@RequiredArgsConstructor
public class DocumentUploadServiceImpl implements DocumentUploadService {

    private final DocumentUploadRepository documentUploadRepository;
    private final FileUtils fileUtils;
    private final FileValidator fileValidator;
    private final UserService userService;
    @Override
    public DocumentUploadEntity uploadDocument(MultipartFile file, String userId) throws InvalidFileException {

        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidFileException("Uploaded file has no name");
        }

        // Extract file extension
        String extension = FileUtils.getFileExtension(originalFilename);

        // Validate extension (throws if unsupported)
        FileUtils.getFileExtension(extension);

        // ─────────────────────────────────────────────
        // STORE METADATA IN DATABASE
        // ─────────────────────────────────────────────

        UserEntity user = userService.getById(userId);

        DocumentUploadEntity entity = DocumentUploadEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .originalFilename(originalFilename)
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        DocumentUploadEntity saved = documentUploadRepository.save(entity);

        // You may store file physically (local or S3)
        // NOT IMPLEMENTED HERE — depends on your chosen storage path.
        // file.transferTo(...)

        return saved;
    }


    @Override
    public DocumentUploadEntity getById(String documentId) throws InvalidFileException {
        return documentUploadRepository.findById(documentId)
                .orElseThrow(() -> new InvalidFileException("Document not found: " + documentId));
    }
}