package com.aissummarizer.jennet.summarization.util;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletion.Choice;

import java.util.List;
import java.util.Optional;

/**
 * Utility class for extracting response content from OpenAI ChatCompletion objects.
 * <p>
 * This class provides methods to safely extract text content from the ChatCompletion
 * response returned by the OpenAI SDK's ChatCompletions API.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create chat completion request
 * ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
 *     .model("gpt-4o-mini")
 *     .addMessage(userMessage)
 *     .build();
 *
 * // Call OpenAI API
 * ChatCompletion completion = client.chat().completions().create(params);
 *
 * // Extract response content
 * String response = ChatCompletionResponseExtractor.extractContent(completion);
 * }</pre>
 *
 * @see com.openai.models.chat.completions.ChatCompletion
 */
public final class ChatCompletionResponseExtractor {

    private ChatCompletionResponseExtractor() {
        // Utility class - prevent instantiation
    }

    /**
     * Extracts the text content from the last choice in a ChatCompletion response.
     * <p>
     * The OpenAI ChatCompletions API returns a response with one or more choices.
     * Each choice contains a message with content. This method retrieves the content
     * from the last choice in the list.
     * </p>
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return the text content from the last choice, or an empty string if no content is available
     * @throws IllegalArgumentException if completion is null
     */
    public static String extractContent(ChatCompletion completion) {
        if (completion == null) {
            throw new IllegalArgumentException("ChatCompletion cannot be null");
        }

        List<Choice> choices = completion.choices();
        if (choices == null || choices.isEmpty()) {
            return "";
        }

        return choices.getLast()
                .message()
                .content()
                .orElse("");
    }

    /**
     * Extracts the text content from the first choice in a ChatCompletion response.
     * <p>
     * This method is useful when you want to get the first (and typically only)
     * choice from the response.
     * </p>
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return the text content from the first choice, or an empty string if no content is available
     * @throws IllegalArgumentException if completion is null
     */
    public static String extractFirstContent(ChatCompletion completion) {
        if (completion == null) {
            throw new IllegalArgumentException("ChatCompletion cannot be null");
        }

        List<Choice> choices = completion.choices();
        if (choices == null || choices.isEmpty()) {
            return "";
        }

        return choices.getFirst()
                .message()
                .content()
                .orElse("");
    }

    /**
     * Extracts the text content as an Optional from the last choice in a ChatCompletion response.
     * <p>
     * This method returns an Optional, allowing for more flexible handling of
     * missing or empty content.
     * </p>
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return an Optional containing the text content, or Optional.empty() if no content is available
     */
    public static Optional<String> extractContentOptional(ChatCompletion completion) {
        if (completion == null) {
            return Optional.empty();
        }

        List<Choice> choices = completion.choices();
        if (choices == null || choices.isEmpty()) {
            return Optional.empty();
        }

        return choices.getLast().message().content();
    }

    /**
     * Extracts the finish reason from the last choice in a ChatCompletion response.
     * <p>
     * The finish reason indicates why the model stopped generating tokens.
     * Common values include:
     * <ul>
     *   <li>{@code stop} - The model completed the response naturally</li>
     *   <li>{@code length} - The response was cut off due to max_tokens limit</li>
     *   <li>{@code content_filter} - Content was filtered</li>
     * </ul>
     * </p>
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return an Optional containing the finish reason, or Optional.empty() if not available
     */
    public static Optional<Choice.FinishReason> extractFinishReason(ChatCompletion completion) {
        if (completion == null) {
            return Optional.empty();
        }

        List<Choice> choices = completion.choices();
        if (choices == null || choices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(choices.getLast().finishReason());
    }

    /**
     * Extracts the total token usage from a ChatCompletion response.
     * <p>
     * Returns the total number of tokens used in both the prompt and completion.
     * </p>
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return the total token count, or 0 if usage information is not available
     */
    public static long extractTotalTokens(ChatCompletion completion) {
        if (completion == null) {
            return 0;
        }

        return completion.usage()
                .map(usage -> usage.totalTokens())
                .orElse(0L);
    }

    /**
     * Extracts detailed token usage from a ChatCompletion response.
     *
     * @param completion the ChatCompletion response from the OpenAI API
     * @return a TokenUsage record containing prompt, completion, and total token counts
     */
    public static TokenUsage extractTokenUsage(ChatCompletion completion) {
        if (completion == null) {
            return new TokenUsage(0, 0, 0);
        }

        return completion.usage()
                .map(usage -> new TokenUsage(
                        usage.promptTokens(),
                        usage.completionTokens(),
                        usage.totalTokens()))
                .orElse(new TokenUsage(0, 0, 0));
    }

    /**
     * Record representing token usage information from a ChatCompletion response.
     *
     * @param promptTokens     the number of tokens in the prompt
     * @param completionTokens the number of tokens in the completion
     * @param totalTokens      the total number of tokens used
     */
    public record TokenUsage(long promptTokens, long completionTokens, long totalTokens) {
    }
}
