package com.aissummarizer.jennet.exceptions;

/**
 * Thrown when file validation fails
 */
public class InvalidFileException extends DocumentProcessingException {

    public InvalidFileException(String message) {
        super(message);
    }
}