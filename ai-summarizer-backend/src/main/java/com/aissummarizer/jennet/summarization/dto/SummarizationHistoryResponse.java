package com.aissummarizer.jennet.summarization.dto;

import com.aissummarizer.jennet.summarization.entity.SummarizationHistoryEntity;

import java.time.LocalDateTime;

/**
 * Response DTO for a summarization history entry.
 */
public record SummarizationHistoryResponse(
        String id,
        String inputText,
        String summaryText,
        LocalDateTime createdAt
) {

    public static SummarizationHistoryResponse from(SummarizationHistoryEntity e) {
        return new SummarizationHistoryResponse(
                e.getId(),
                e.getInputText(),
                e.getSummaryText(),
                e.getCreatedAt()
        );
    }
}