package com.aissummarizer.jennet.common.exception;

/**
 * Exception representing a resource that could not be found (HTTP 404).
 * <p>
 * Used when entities such as users, tokens, or domain objects
 * cannot be located in persistence.
 */
public class NotFoundException extends BusinessException {

    /**
     * Creates a new not found exception with the given message.
     *
     * @param message descriptive error message
     */
    public NotFoundException(String message) {
        super(message);
    }
}