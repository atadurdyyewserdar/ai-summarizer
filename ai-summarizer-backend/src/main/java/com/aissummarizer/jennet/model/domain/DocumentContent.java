package com.aissummarizer.jennet.model.domain;

import com.aissummarizer.jennet.model.enums.DocumentType;

import java.util.List;

/**
 * Base interface for all document content types
 */
public interface DocumentContent {
    /**
     * Get all text content concatenated
     * @return Full text content
     */
    String getAllText();

    /**
     * Get approximate word count
     * @return Word count
     */
    int getWordCount();

    /**
     * Get document type
     * @return Document type
     */
    DocumentType getType();

    /**
     * Check if document contains images
     * @return true if has images
     */
    boolean hasImages();

    /**
     * Get all images in document
     * @return List of images, empty list if none
     */
    List<ImageData> getImages();
}
