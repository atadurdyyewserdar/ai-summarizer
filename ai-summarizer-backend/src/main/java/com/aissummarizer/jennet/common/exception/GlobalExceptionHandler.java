package com.aissummarizer.jennet.common.exception;

import com.aissummarizer.jennet.common.enums.ErrorCode;
import com.aissummarizer.jennet.common.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {

        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, errors));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException ex) {

        String message = "Missing required parameter: " + ex.getParameterName();
        log.warn(message);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.MISSING_PARAMETER, message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex) {

        log.warn("File size exceeded maximum allowed");

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error(
                        ErrorCode.FILE_TOO_LARGE,
                        "File size exceeds maximum allowed size (50MB)"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_ERROR,
                        "An unexpected error occurred"
                ));
    }

    /**
     * Handles cases where a requested resource was not found.
     *
     * @param ex thrown exception
     * @return HTTP 404 with error details
    */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(java.util.Map.of("error", ex.getMessage()));
    }

    /**
     * Handles input validation and similar client-side errors.
     *
     * @param ex thrown exception
     * @return HTTP 400 with error details
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", ex.getMessage()));
    }

    /**
     * Handles any business rule violation that isn't a bad request or not found.
     *
     * @param ex thrown exception
     * @return HTTP 422 with error details
     */
    @ExceptionHandler(BusinessException.class)
        public ResponseEntity<?> handleBusiness(BusinessException ex) {
           return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(java.util.Map.of("error", ex.getMessage()));
     }

    /**
     * Handles AI summarization failures, including network connectivity issues.
     *
     * @param ex thrown exception
     * @return HTTP 503 for network issues, HTTP 500 for other AI service errors
     */
    @ExceptionHandler(AiSummarizationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiSummarizationException(
            AiSummarizationException ex) {
        
        Throwable cause = ex.getCause();
        
        // Check if it's a network connectivity issue
        if (isNetworkException(cause)) {
            log.error("AI service unavailable due to network issues: {}", cause.getMessage());
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(
                            ErrorCode.AI_SERVICE_ERROR,
                            "AI service is temporarily unavailable. Please check your network connection and try again."
                    ));
        }
        
        log.error("AI summarization failed", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.AI_SERVICE_ERROR,
                        ex.getMessage()
                ));
    }

    /**
     * Checks if the exception is related to network connectivity issues.
     */
    private boolean isNetworkException(Throwable cause) {
        if (cause == null) {
            return false;
        }
        
        // Check the exception and its cause chain for network-related exceptions
        Throwable current = cause;
        while (current != null) {
            if (current instanceof java.net.UnknownHostException ||
                current instanceof java.net.ConnectException ||
                current instanceof java.net.SocketTimeoutException ||
                current instanceof java.net.SocketException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

}