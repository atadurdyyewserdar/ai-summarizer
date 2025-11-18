package com.aissummarizer.jennet.extractor;

import com.aissummarizer.jennet.exceptions.DocumentProcessingException;
import com.aissummarizer.jennet.model.domain.DocumentContent;
import com.aissummarizer.jennet.model.enums.DocumentType;

import java.io.InputStream;

/**
 * Strategy pattern for different document extractors
 * @param <T> Type of document content this extractor produces
 */
public interface DocumentExtractor<T extends DocumentContent> {
    /**
     * Extract content from input stream
     * @param inputStream The document input stream
     * @return Extracted document content
     * @throws DocumentProcessingException if extraction fails
     */
    T extract(InputStream inputStream) throws DocumentProcessingException;

    /**
     * Check if this extractor supports given file extension
     * @param fileExtension File extension without dot (e.g., "pptx")
     * @return true if supported
     */
    boolean supports(String fileExtension);

    /**
     * Get the document type this extractor handles
     * @return Document type
     */
    DocumentType getDocumentType();
}