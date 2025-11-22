package com.aissummarizer.jennet.user.dto;


import java.time.LocalDateTime;

/**
 * DTO representing a single summarization history entry for a user.
 */
public record UserSummarizationHistoryResponse(
        String id,
        String originalTextPreview,
        String summarizedTextPreview,
        LocalDateTime createdAt
) {}