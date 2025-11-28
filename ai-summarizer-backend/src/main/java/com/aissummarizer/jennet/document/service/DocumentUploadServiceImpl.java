package com.aissummarizer.jennet.document.service;

import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.common.exception.UnsupportedDocumentTypeException;
import com.aissummarizer.jennet.common.validator.FileValidator;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.repository.DocumentUploadRepository;
import com.aissummarizer.jennet.document.tools.FileUtils;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DocumentUploadServiceImpl implements DocumentUploadService {
    private final FileValidator fileValidator;
    private final DocumentUploadRepository documentUploadRepository;
    private final UserService userService;

    @Override
    public DocumentUploadEntity uploadDocument(MultipartFile file, String userName) throws InvalidFileException, UnsupportedDocumentTypeException {

        fileValidator.validate(file);

        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidFileException("Uploaded file has no name");
        }

        // Validate extension (throws if unsupported)
        FileUtils.getFileExtension(originalFilename);

        // STORE METADATA IN DATABASE
        UserEntity user = userService.getByUserName(userName);

        DocumentUploadEntity entity = DocumentUploadEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .originalFilename(originalFilename)
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .documentType(DocumentType.fromExtension(FileUtils.getFileExtension(file.getOriginalFilename())))
                .fileExtension(FileUtils.getFileExtension(file.getOriginalFilename()))
                .build();
        return documentUploadRepository.save(entity);
    }

    /**
     * If user sends text instead of file, we need to create MockDocument to save smth in DB
     * @param customText users text
     * @param userName users username
     * @return DocumentUploadEntity
     */
    @Override
    public DocumentUploadEntity uploadMockDocument(String customText, String userName) throws InvalidFileException {
        if (customText.isBlank()) {
            throw new InvalidFileException("Text is blank");
        }

        String originalFilename = customText.substring(0, customText.indexOf(" "));
        if (originalFilename.isEmpty()) {
            throw new InvalidFileException("Text is blank");
        }

        // STORE METADATA IN DATABASE
        UserEntity user = userService.getByUserName(userName);

        DocumentUploadEntity entity = DocumentUploadEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .originalFilename(originalFilename)
                .fileSize(customText.getBytes().length)
                .uploadedAt(LocalDateTime.now())
                .documentType(DocumentType.TXT)
                .fileExtension("TXT")
                .build();

        return documentUploadRepository.save(entity);
    }


    @Override
    public DocumentUploadEntity getById(String documentId) throws InvalidFileException {
        return documentUploadRepository.findById(documentId)
                .orElseThrow(() -> new InvalidFileException("Document not found: " + documentId));
    }
}