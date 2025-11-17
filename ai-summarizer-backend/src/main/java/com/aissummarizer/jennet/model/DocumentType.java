package com.aissummarizer.jennet.model;

import com.aissummarizer.jennet.exceptions.UnsupportedDocumentTypeException;

import java.util.Objects;

public enum DocumentType {
    PPTX("pptx", "PowerPoint Presentation"),
    DOCX("docx", "Word Document"),
    TXT("txt", "Text Document"),
    PDF("pdf", "PDF Document");

    private final String extension;
    private final String description;

    DocumentType(String extension, String description) {
        this.extension = Objects.requireNonNull(extension, "Extension cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get DocumentType from file extension
     * @param extension File extension (with or without dot)
     * @return DocumentType
     * @throws UnsupportedDocumentTypeException if not supported
     */
    public static DocumentType fromExtension(String extension)
            throws UnsupportedDocumentTypeException {

        if (extension == null || extension.isBlank()) {
            throw new UnsupportedDocumentTypeException("null or empty");
        }

        // Remove leading dot if present
        String cleanExtension = extension.startsWith(".")
                ? extension.substring(1)
                : extension;

        for (DocumentType type : values()) {
            if (type.extension.equalsIgnoreCase(cleanExtension.trim())) {
                return type;
            }
        }

        throw new UnsupportedDocumentTypeException(cleanExtension);
    }
}
