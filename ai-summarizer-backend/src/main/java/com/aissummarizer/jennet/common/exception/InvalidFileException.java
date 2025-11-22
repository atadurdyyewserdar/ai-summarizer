package com.aissummarizer.jennet.common.exception;

/**
 * Thrown when file validation fails
 */
public class InvalidFileException extends DocumentProcessingException {

    public InvalidFileException(String message) {
        super(message);
    }
}