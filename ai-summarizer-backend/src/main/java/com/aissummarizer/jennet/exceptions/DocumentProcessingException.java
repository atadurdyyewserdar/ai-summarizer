package com.aissummarizer.jennet.exceptions;

/**
 * Base exception for document processing errors
 */
public class DocumentProcessingException extends Exception {
    public DocumentProcessingException(String message) {
        super(message);
    }

    public DocumentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
