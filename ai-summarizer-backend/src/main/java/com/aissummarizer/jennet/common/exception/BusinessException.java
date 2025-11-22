package com.aissummarizer.jennet.common.exception;


/**
 * Base class for application-specific runtime exceptions.
 * <p>
 * All custom business-level exceptions should inherit from this class
 * to allow global exception handling to distinguish them from
 * unexpected system-level failures.
 */
public abstract class BusinessException extends RuntimeException {

    /**
     * Creates a new business exception with the given message.
     *
     * @param message descriptive error message
     */
    public BusinessException(String message) {
        super(message);
    }
}