package com.aissummarizer.jennet.apilog.dto;

public record ApiUsageResponseDto (String endpoint, String httpMethod, String userName, long processingTime){
}
