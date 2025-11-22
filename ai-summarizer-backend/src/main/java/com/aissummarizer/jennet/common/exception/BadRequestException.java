package com.aissummarizer.jennet.common.exception;

/**
 * Exception representing a client-side request error (HTTP 400).
 * <p>
 * This is typically thrown when user input is invalid or violates
 * validation/business rules.
 */
public class BadRequestException extends BusinessException {

    /**
     * Creates a new bad request exception with the given message.
     *
     * @param message descriptive error message
     */
    public BadRequestException(String message) {
        super(message);
    }
}