package com.aissummarizer.jennet.apilog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ApiUsageResponseDto (
        String endpoint,
        String httpMethod,
        String userName,
        long processingTime,
        @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
        LocalDateTime createdAt
        ){
}
