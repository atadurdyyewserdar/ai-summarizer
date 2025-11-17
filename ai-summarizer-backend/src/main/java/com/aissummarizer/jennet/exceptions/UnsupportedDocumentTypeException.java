package com.aissummarizer.jennet.exceptions;

/**
 * Thrown when document type is not supported
 */
public class UnsupportedDocumentTypeException extends DocumentProcessingException {

    private final String fileType;

    public UnsupportedDocumentTypeException(String fileType) {
        super("Unsupported document type: " + fileType +
                ". Supported types: pptx, docx, txt, pdf");
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}