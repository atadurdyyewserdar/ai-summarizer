package com.aissummarizer.jennet.common.model;

import com.aissummarizer.jennet.common.enums.ErrorCode;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class ApiResponse<T> {
    // Getters
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

        public record ErrorInfo(ErrorCode code, String message) {
            public ErrorInfo(ErrorCode code, String message) {
                this.code = Objects.requireNonNull(code);
                this.message = Objects.requireNonNull(message);
            }

        }
}

