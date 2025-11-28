package com.aissummarizer.jennet.summarization.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Response DTO for a summarization history entry.
 */
public record SummarizationHistoryResponse(
        String id,
        String inputText,
        String summaryText,
        @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")LocalDateTime createdAt
) {

//    public static SummarizationHistoryResponse from(SummarizationHistoryEntity e) {
//        return new SummarizationHistoryResponse(
//                e.getId(),
//                e.getInputText(),
//                e.getSummaryText(),
//                e.getCreatedAt()
//        );
//    }
}