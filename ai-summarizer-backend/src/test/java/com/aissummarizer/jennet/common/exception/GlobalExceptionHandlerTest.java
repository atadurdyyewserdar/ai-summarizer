package com.aissummarizer.jennet.common.exception;

import com.aissummarizer.jennet.common.enums.ErrorCode;
import com.aissummarizer.jennet.common.model.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleAiSummarizationException should return 503 for UnknownHostException")
    void handleAiSummarizationException_shouldReturn503ForUnknownHostException() {
        // Given
        UnknownHostException networkException = new UnknownHostException("api.openai.com");
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", networkException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getError());
        assertEquals(ErrorCode.AI_SERVICE_ERROR, response.getBody().getError().code());
        assertTrue(response.getBody().getError().message().contains("temporarily unavailable"));
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 503 for ConnectException")
    void handleAiSummarizationException_shouldReturn503ForConnectException() {
        // Given
        ConnectException networkException = new ConnectException("Connection refused");
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", networkException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 503 for SocketTimeoutException")
    void handleAiSummarizationException_shouldReturn503ForSocketTimeoutException() {
        // Given
        SocketTimeoutException networkException = new SocketTimeoutException("Connection timed out");
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", networkException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 503 for SocketException")
    void handleAiSummarizationException_shouldReturn503ForSocketException() {
        // Given
        SocketException networkException = new SocketException("Network is unreachable");
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", networkException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 503 for nested network exception")
    void handleAiSummarizationException_shouldReturn503ForNestedNetworkException() {
        // Given - network exception wrapped in another exception
        UnknownHostException networkException = new UnknownHostException("api.openai.com");
        RuntimeException wrappedException = new RuntimeException("Wrapper", networkException);
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", wrappedException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 500 for non-network exceptions")
    void handleAiSummarizationException_shouldReturn500ForNonNetworkException() {
        // Given
        RuntimeException otherException = new RuntimeException("Some other error");
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed", otherException);

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getError());
        assertEquals(ErrorCode.AI_SERVICE_ERROR, response.getBody().getError().code());
    }

    @Test
    @DisplayName("handleAiSummarizationException should return 500 when no cause")
    void handleAiSummarizationException_shouldReturn500WhenNoCause() {
        // Given
        AiSummarizationException ex = new AiSummarizationException("AI summarization failed");

        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleAiSummarizationException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
