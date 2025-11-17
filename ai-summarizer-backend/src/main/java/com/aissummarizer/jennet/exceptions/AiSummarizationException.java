package com.aissummarizer.jennet.exceptions;

/**
 * Thrown when AI summarization fails
 */
public class AiSummarizationException extends RuntimeException {

    public AiSummarizationException(String message) {
        super(message);
    }

    public AiSummarizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
