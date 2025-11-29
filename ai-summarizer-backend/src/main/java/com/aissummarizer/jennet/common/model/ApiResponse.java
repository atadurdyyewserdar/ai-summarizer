package com.aissummarizer.jennet.common.model;

import com.aissummarizer.jennet.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
public final class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorInfo error;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Europe/Minsk")
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

