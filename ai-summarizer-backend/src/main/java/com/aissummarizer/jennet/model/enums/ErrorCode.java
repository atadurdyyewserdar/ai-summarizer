package com.aissummarizer.jennet.model.enums;

public enum ErrorCode {
    UNSUPPORTED_FILE_TYPE("ERR_001", "Unsupported file type"),
    INVALID_FILE("ERR_002", "Invalid file"),
    FILE_TOO_LARGE("ERR_003", "File too large"),
    VALIDATION_ERROR("ERR_004", "Validation error"),
    MISSING_PARAMETER("ERR_005", "Missing parameter"),
    PROCESSING_ERROR("ERR_006", "Processing error"),
    AI_SERVICE_ERROR("ERR_007", "AI service error"),
    INTERNAL_ERROR("ERR_999", "Internal server error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}

