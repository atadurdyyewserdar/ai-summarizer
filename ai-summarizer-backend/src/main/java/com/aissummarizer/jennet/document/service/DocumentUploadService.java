package com.aissummarizer.jennet.document.service;


import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.common.exception.UnsupportedDocumentTypeException;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for handling document upload operations.
 * <p>
 * Responsibilities:
 * - Validating uploaded files
 * - Storing file metadata in DB
 * - Returning DocumentUploadEntity records
 * - Passing raw InputStreams to extractors when needed
 */
public interface DocumentUploadService {

    /**
     * Uploads a document, validates it, stores metadata in DB,
     * and returns the saved DocumentUploadEntity.
     *
     * @param file the uploaded file
     * @param userId the user uploading the file
     * @return saved DocumentUploadEntity
     */
    DocumentUploadEntity uploadDocument(MultipartFile file, String userId) throws InvalidFileException, UnsupportedDocumentTypeException;


    /**
     * Retrieves a previously uploaded document metadata record.
     *
     * @param documentId database UUID
     * @return DocumentUploadEntity
     */
    DocumentUploadEntity getById(String documentId) throws InvalidFileException;
}