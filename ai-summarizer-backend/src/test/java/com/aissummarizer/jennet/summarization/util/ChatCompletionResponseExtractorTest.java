package com.aissummarizer.jennet.summarization.util;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletion.Choice;
import com.openai.models.chat.completions.ChatCompletionMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ChatCompletionResponseExtractor}.
 */
class ChatCompletionResponseExtractorTest {

    @Test
    @DisplayName("extractContent should return content from the last choice")
    void extractContent_shouldReturnContentFromLastChoice() {
        // Given
        String expectedContent = "This is the AI response";
        ChatCompletion completion = createMockCompletion(expectedContent);

        // When
        String result = ChatCompletionResponseExtractor.extractContent(completion);

        // Then
        assertEquals(expectedContent, result);
    }

    @Test
    @DisplayName("extractContent should return empty string when completion has no choices")
    void extractContent_shouldReturnEmptyStringWhenNoChoices() {
        // Given
        ChatCompletion completion = mock(ChatCompletion.class);
        when(completion.choices()).thenReturn(List.of());

        // When
        String result = ChatCompletionResponseExtractor.extractContent(completion);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("extractContent should throw IllegalArgumentException when completion is null")
    void extractContent_shouldThrowExceptionWhenNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> ChatCompletionResponseExtractor.extractContent(null));
    }

    @Test
    @DisplayName("extractFirstContent should return content from the first choice")
    void extractFirstContent_shouldReturnContentFromFirstChoice() {
        // Given
        String expectedContent = "First choice content";
        ChatCompletion completion = createMockCompletion(expectedContent);

        // When
        String result = ChatCompletionResponseExtractor.extractFirstContent(completion);

        // Then
        assertEquals(expectedContent, result);
    }

    @Test
    @DisplayName("extractContentOptional should return Optional with content")
    void extractContentOptional_shouldReturnOptionalWithContent() {
        // Given
        String expectedContent = "This is the AI response";
        ChatCompletion completion = createMockCompletion(expectedContent);

        // When
        Optional<String> result = ChatCompletionResponseExtractor.extractContentOptional(completion);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedContent, result.get());
    }

    @Test
    @DisplayName("extractContentOptional should return empty Optional when completion is null")
    void extractContentOptional_shouldReturnEmptyWhenNull() {
        // When
        Optional<String> result = ChatCompletionResponseExtractor.extractContentOptional(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("extractTotalTokens should return 0 when completion is null")
    void extractTotalTokens_shouldReturnZeroWhenNull() {
        // When
        long result = ChatCompletionResponseExtractor.extractTotalTokens(null);

        // Then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("extractTokenUsage should return zero values when completion is null")
    void extractTokenUsage_shouldReturnZeroValuesWhenNull() {
        // When
        ChatCompletionResponseExtractor.TokenUsage result = 
            ChatCompletionResponseExtractor.extractTokenUsage(null);

        // Then
        assertEquals(0, result.promptTokens());
        assertEquals(0, result.completionTokens());
        assertEquals(0, result.totalTokens());
    }

    @Test
    @DisplayName("extractFinishReason should return empty Optional when completion is null")
    void extractFinishReason_shouldReturnEmptyWhenNull() {
        // When
        Optional<Choice.FinishReason> result = 
            ChatCompletionResponseExtractor.extractFinishReason(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("extractFinishReason should return empty Optional when no choices")
    void extractFinishReason_shouldReturnEmptyWhenNoChoices() {
        // Given
        ChatCompletion completion = mock(ChatCompletion.class);
        when(completion.choices()).thenReturn(List.of());

        // When
        Optional<Choice.FinishReason> result = 
            ChatCompletionResponseExtractor.extractFinishReason(completion);

        // Then
        assertTrue(result.isEmpty());
    }

    /**
     * Helper method to create a mock ChatCompletion with the given content.
     */
    private ChatCompletion createMockCompletion(String content) {
        ChatCompletion completion = mock(ChatCompletion.class);
        Choice choice = mock(Choice.class);
        ChatCompletionMessage message = mock(ChatCompletionMessage.class);

        when(message.content()).thenReturn(Optional.of(content));
        when(choice.message()).thenReturn(message);
        when(completion.choices()).thenReturn(List.of(choice));

        return completion;
    }
}
