package com.aissummarizer.jennet.user.dto;


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
        LocalDateTime createdAt,
        String inputText,
        String summaryText,
        String summaryType,
        String documentType,
        String fileName
) {
}