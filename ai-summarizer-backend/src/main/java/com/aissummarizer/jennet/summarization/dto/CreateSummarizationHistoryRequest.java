package com.aissummarizer.jennet.summarization.dto;

/**
 * Request DTO to store a summarization result in history.
 * <p>
 * This assumes the summary is generated elsewhere (backend or frontend)
 * and then sent here to be persisted.
 */
public record CreateSummarizationHistoryRequest(
        String inputText,
        String summaryText
) { }