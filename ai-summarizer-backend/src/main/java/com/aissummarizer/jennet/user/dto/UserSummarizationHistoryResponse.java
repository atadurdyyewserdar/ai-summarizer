package com.aissummarizer.jennet.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Response DTO representing a single summarization history entry.
 *
 * <p>Returned by:
 *  - GET /api/user/history
 *  - Included inside profile pages if needed
 *
 * <p>This DTO is intentionally lightweight and contains only
 * user-friendly preview fields.
 */
public record UserSummarizationHistoryResponse(
        String id,
        @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
        LocalDateTime createdAt,
        String summaryText,
        String summaryType,
        String documentType,
        String fileName,
        int imageCount,
        int paragraphCount,
        int slideCount,
        long processingTime,
        int tableCount,
        int wordCount,
        long fileSize
) {
}