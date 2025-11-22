package com.aissummarizer.jennet.common.model;

import com.aissummarizer.jennet.common.enums.ErrorCode;

import java.util.Objects;

public final class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorInfo error;
    private final long timestamp;

    private ApiResponse(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message));
    }

    // Getters
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public ErrorInfo getError() { return error; }
    public long getTimestamp() { return timestamp; }

    /**
     * Error information
     */
    public static final class ErrorInfo {
        private final ErrorCode code;
        private final String message;

        public ErrorInfo(ErrorCode code, String message) {
            this.code = Objects.requireNonNull(code);
            this.message = Objects.requireNonNull(message);
        }

        public ErrorCode getCode() { return code; }
        public String getMessage() { return message; }
    }
}

